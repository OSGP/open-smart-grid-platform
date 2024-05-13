// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MacGenerationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareImageIdentifierCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ImageTransferException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateFirmwareCommandExecutor
    extends AbstractCommandExecutor<UpdateFirmwareRequestDto, UpdateFirmwareResponseDto> {

  private static final String EXCEPTION_MSG_UPDATE_FAILED = "Upgrade of firmware did not succeed.";

  private static final String EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE =
      "Firmware file is not available.";
  private static final String EXCEPTION_MSG_FIRMWARE_IMAGE_IDENTIFIER_NOT_AVAILABLE =
      "Firmware Image Identifier is not available.";
  private static final String EXCEPTION_MSG_DEVICE_NOT_AVAILABLE_IN_DATABASE =
      "Device %s not available in database.";
  private static final String EXCEPTION_MSG_DEVICE_HAS_NO_MBUS_IDENTIFICATION_NUMBER =
      "Device %s has no M-Bus identification number.";
  private static final String SHA256 = "SHA-256";

  private final DlmsDeviceRepository dlmsDeviceRepository;

  private final FirmwareFileCachingRepository firmwareFileCachingRepository;
  private final FirmwareImageIdentifierCachingRepository firmwareImageIdentifierCachingRepository;
  private final MacGenerationService macGenerationService;
  private final ImageTransfer.ImageTransferProperties imageTransferProperties;

  public UpdateFirmwareCommandExecutor(
      final DlmsDeviceRepository dlmsDeviceRepository,
      final FirmwareFileCachingRepository firmwareFileCachingRepository,
      final FirmwareImageIdentifierCachingRepository firmwareImageIdentifierCachingRepository,
      final MacGenerationService macGenerationService,
      final ImageTransfer.ImageTransferProperties imageTransferProperties) {
    super(UpdateFirmwareRequestDto.class);
    this.dlmsDeviceRepository = dlmsDeviceRepository;
    this.firmwareFileCachingRepository = firmwareFileCachingRepository;
    this.firmwareImageIdentifierCachingRepository = firmwareImageIdentifierCachingRepository;
    this.macGenerationService = macGenerationService;
    this.imageTransferProperties = imageTransferProperties;
  }

  @Override
  public UpdateFirmwareResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final String firmwareIdentification = getFirmwareIdentification(updateFirmwareRequestDto);
    final FirmwareFile firmwareFile =
        this.getFirmwareFile(updateFirmwareRequestDto, messageMetadata);

    final ImageTransfer transfer =
        new ImageTransfer(
            conn,
            this.imageTransferProperties,
            this.getImageIdentifier(firmwareIdentification, firmwareFile),
            firmwareFile.getByteArray());

    // Calculate the hash of the FW file
    // for mbus device get a part of the image file because we add a mac to it
    // for e-meter use whole file because that is static
    final String calculatedHash =
        this.calculateHash(
            firmwareFile.isMbusFirmware()
                ? firmwareFile.getFirmwareImageByteArray()
                : firmwareFile.getByteArray(),
            SHA256);

    // Resume on the last not transferred block if
    // a hash of a previously uploaded FW file is stored with this device
    // and this hash is equal to the hash of the FW file to be uploaded
    // and the image_transfer_status is INITIATED
    final boolean resumeOnLastBlock =
        device.getFirmwareHash() != null
            && device.getFirmwareHash().equals(calculatedHash)
            && transfer.isInitiated();

    try {
      if (resumeOnLastBlock) {
        this.enable(transfer);
        // do not call method image_transfer_initiate (in this class' prepare method).
        // This method will reset first_not_transferred_block_number to 0 (zero)
        // This assumes that the image_transfer_status attribute is (1) Image transfer initiated.
        final int blockNumber = transfer.getImageFirstNotTransferredBlockNumber();
        this.transfer(transfer, blockNumber);
      } else {
        this.storeFirmwareHashWithDlmsDevice(device, calculatedHash);
        this.prepare(transfer);
        this.transfer(transfer, 0);
      }
      // After completing the transfer removed the Firmware hash stored with the device
      this.removeFirmwareHashFromDlmsDevice(device);
      if (!firmwareFile.isMbusFirmware()) {
        this.verify(transfer);
        this.activate(transfer);
      } else {
        this.activateWithoutVerification(transfer);
      }
      return new UpdateFirmwareResponseDto(firmwareIdentification);
    } catch (final ImageTransferException | ProtocolAdapterException e) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_UPDATE_FAILED, e);
    }
  }

  private static String getFirmwareIdentification(
      final UpdateFirmwareRequestDto updateFirmwareRequestDto) {
    return updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto().getFirmwareIdentification();
  }

  private void prepare(final ImageTransfer transfer) throws ProtocolAdapterException {
    this.enable(transfer);
    transfer.initiateImageTransfer();
  }

  private void enable(final ImageTransfer transfer) throws ProtocolAdapterException {
    if (!transfer.imageTransferEnabled()) {
      transfer.setImageTransferEnabled(true);
    }
  }

  private void transfer(final ImageTransfer transfer, final int firstBlock) throws OsgpException {
    if (transfer.shouldTransferImage()) {
      transfer.transferImageBlocks(firstBlock);
      transfer.transferMissingImageBlocks();
    } else {
      log.info("The current ImageTransferStatus is not INITIATED");
    }
  }

  private void verify(final ImageTransfer transfer) throws OsgpException {
    if (!transfer.imageIsVerified()) {
      transfer.verifyImage();
    }
  }

  private void activate(final ImageTransfer transfer) throws OsgpException {
    if (transfer.imageIsVerified() && transfer.imageToActivateOk()) {
      transfer.activateImage();
    } else {
      throw new ProtocolAdapterException("An unknown error occurred while updating firmware.");
    }
  }

  private void activateWithoutVerification(final ImageTransfer transfer) throws OsgpException {
    transfer.activateImage();
  }

  private FirmwareFile getFirmwareFile(
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    log.debug(
        "Getting firmware file from caching repository for firmware {}",
        getFirmwareIdentification(updateFirmwareRequestDto));
    final byte[] firmwareFileByteArray =
        this.firmwareFileCachingRepository.retrieve(
            getFirmwareIdentification(updateFirmwareRequestDto));

    if (firmwareFileByteArray == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE);
    }

    final FirmwareFile firmwareFile = new FirmwareFile(firmwareFileByteArray);
    if (firmwareFile.isMbusFirmware()) {

      firmwareFile.checkLengths();

      this.addMac(
          messageMetadata, updateFirmwareRequestDto.getDeviceIdentification(), firmwareFile);
    }

    return firmwareFile;
  }

  private FirmwareFile addMac(
      final MessageMetadata messageMetadata,
      final String deviceIdentification,
      final FirmwareFile firmwareFile)
      throws ProtocolAdapterException {

    log.debug(
        "Adding MAC to firmware file for M-Bus device with deviceIdentification {}",
        deviceIdentification);
    final DlmsDevice mbusDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    if (mbusDevice == null) {
      throw new ProtocolAdapterException(
          String.format(EXCEPTION_MSG_DEVICE_NOT_AVAILABLE_IN_DATABASE, deviceIdentification));
    }

    final String identificationNumber = this.getIdentificationNumber(mbusDevice);

    log.debug("Original Firmware file header: {}", firmwareFile.getHeader());

    log.debug("Setting M-Bus Identification number: {}", identificationNumber);
    firmwareFile.setMbusDeviceIdentificationNumber(identificationNumber);

    final int mbusVersion = 80;
    log.debug("Setting M-Bus Version: {}", mbusVersion);
    firmwareFile.setMbusVersion(mbusVersion);

    log.debug("Modified Firmware file header: {}", firmwareFile.getHeader());

    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(
            messageMetadata, mbusDevice.getDeviceIdentification(), firmwareFile);

    log.debug("Calculated MAC: {}", Hex.toHexString(calculatedMac));

    firmwareFile.setSecurityByteArray(calculatedMac);

    return firmwareFile;
  }

  private String getIdentificationNumber(final DlmsDevice mbusDevice)
      throws ProtocolAdapterException {
    final String mbusIdentificationNumberTextualRepresentation =
        mbusDevice.getMbusIdentificationNumberTextualRepresentation();
    if (mbusIdentificationNumberTextualRepresentation == null) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_DEVICE_HAS_NO_MBUS_IDENTIFICATION_NUMBER,
              mbusDevice.getDeviceIdentification()));
    }
    return mbusIdentificationNumberTextualRepresentation;
  }

  private byte[] getImageIdentifier(
      final String firmwareIdentification, final FirmwareFile firmwareFile)
      throws ProtocolAdapterException {

    final byte[] imageIdentifier;

    if (firmwareFile.isMbusFirmware()) {

      imageIdentifier = firmwareFile.createImageIdentifierForMbusDevice();

    } else {

      log.debug(
          "Getting firmware ImageIdentifier from caching repository for firmware {}",
          firmwareIdentification);

      imageIdentifier = this.getImageIdentifierFromCache(firmwareIdentification);
    }
    log.debug("Firmware ImageIdentifier: {}", Arrays.toString(imageIdentifier));

    return imageIdentifier;
  }

  private byte[] getImageIdentifierFromCache(final String firmwareIdentification)
      throws ProtocolAdapterException {
    final byte[] firmwareImageIdentifier =
        this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification);

    if (firmwareImageIdentifier == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_IMAGE_IDENTIFIER_NOT_AVAILABLE);
    }
    return firmwareImageIdentifier;
  }

  @Override
  public UpdateFirmwareRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    return (UpdateFirmwareRequestDto) bundleInput;
  }

  @Override
  public ActionResponseDto asBundleResponse(final UpdateFirmwareResponseDto executionResult)
      throws ProtocolAdapterException {

    return executionResult;
  }

  private String calculateHash(final byte[] content, final String algorithm) {

    try {
      final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      final byte[] digest = messageDigest.digest(content);
      return new BigInteger(1, digest).toString(16);
    } catch (final NoSuchAlgorithmException e) {
      log.error("Error calculating digest", e);
      return "";
    }
  }

  private void storeFirmwareHashWithDlmsDevice(final DlmsDevice device, final String firmwareHash) {
    log.info(
        "Storing Firmware hash {} for dlms-device {}",
        firmwareHash,
        device.getDeviceIdentification());
    this.dlmsDeviceRepository.storeFirmwareHash(device.getDeviceIdentification(), firmwareHash);
  }

  private void removeFirmwareHashFromDlmsDevice(final DlmsDevice device) {
    log.info("Removing Firmware hash from dlms-device {}", device.getDeviceIdentification());
    this.dlmsDeviceRepository.storeFirmwareHash(device.getDeviceIdentification(), null);
  }
}
