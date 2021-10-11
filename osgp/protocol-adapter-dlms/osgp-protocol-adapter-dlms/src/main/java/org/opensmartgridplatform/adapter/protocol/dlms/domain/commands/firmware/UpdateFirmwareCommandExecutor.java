/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

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
      "Device {} not available in database.";
  private static final String EXCEPTION_MSG_DEVICE_HAS_NO_MBUS_IDENTIFICATION_NUMBER =
      "Device {} has no M-Bus identification number.";

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

    final String firmwareIdentification = updateFirmwareRequestDto.getFirmwareIdentification();
    final FirmwareFile firmwareFile =
        this.getFirmwareFile(updateFirmwareRequestDto, messageMetadata);

    final ImageTransfer transfer =
        new ImageTransfer(
            conn,
            this.imageTransferProperties,
            this.getImageIdentifier(firmwareIdentification, firmwareFile),
            firmwareFile.getByteArray());

    try {
      this.prepare(transfer);
      this.transfer(transfer);
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

  private void prepare(final ImageTransfer transfer) throws ProtocolAdapterException {
    if (!transfer.imageTransferEnabled()) {
      transfer.setImageTransferEnabled(true);
    }

    transfer.initiateImageTransfer();
  }

  private void transfer(final ImageTransfer transfer) throws OsgpException {
    if (transfer.shouldTransferImage()) {
      transfer.transferImageBlocks();
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
        updateFirmwareRequestDto.getFirmwareIdentification());
    final byte[] firmwareFileByteArray =
        this.firmwareFileCachingRepository.retrieve(
            updateFirmwareRequestDto.getFirmwareIdentification());

    if (firmwareFileByteArray == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE);
    }

    final FirmwareFile firmwareFile = new FirmwareFile(firmwareFileByteArray);
    if (firmwareFile.isMbusFirmware()) {
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

    byte[] imageIdentifier = null;

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
}
