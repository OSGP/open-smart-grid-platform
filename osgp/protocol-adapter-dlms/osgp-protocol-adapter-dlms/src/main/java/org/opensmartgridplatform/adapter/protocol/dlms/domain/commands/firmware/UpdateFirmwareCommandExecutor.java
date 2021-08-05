/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MacGenerationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UpdateFirmwareCommandExecutor
    extends AbstractCommandExecutor<String, UpdateFirmwareResponseDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFirmwareCommandExecutor.class);

  private static final String EXCEPTION_MSG_UPDATE_FAILED = "Upgrade of firmware did not succeed.";

  private static final String EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE =
      "Firmware file is not available.";
  private static final String EXCEPTION_MSG_FIRMWARE_IMAGE_IDENTIFIER_NOT_AVAILABLE =
      "Firmware Image Identifier is not available.";

  private final FirmwareFileCachingRepository firmwareFileCachingRepository;
  private final FirmwareImageIdentifierCachingRepository firmwareImageIdentifierCachingRepository;
  private final ImageTransfer.ImageTranferProperties imageTransferProperties;
  private final MacGenerationService macGenerationService;

  public UpdateFirmwareCommandExecutor(
      final FirmwareFileCachingRepository firmwareFileCachingRepository,
      final FirmwareImageIdentifierCachingRepository firmwareImageIdentifierCachingRepository,
      final MacGenerationService macGenerationService,
      @Value("${command.updatefirmware.verificationstatuscheck.interval}")
          final int verificationStatusCheckInterval,
      @Value("${command.updatefirmware.verificationstatuscheck.timeout}")
          final int verificationStatusCheckTimeout,
      @Value("${command.updatefirmware.initiationstatuscheck.interval}")
          final int initiationStatusCheckInterval,
      @Value("${command.updatefirmware.initiationstatuscheck.timeout}")
          final int initiationStatusCheckTimeout) {
    super(UpdateFirmwareRequestDto.class);
    this.firmwareFileCachingRepository = firmwareFileCachingRepository;
    this.firmwareImageIdentifierCachingRepository = firmwareImageIdentifierCachingRepository;
    this.macGenerationService = macGenerationService;

    this.imageTransferProperties = new ImageTransfer.ImageTranferProperties();
    this.imageTransferProperties.setVerificationStatusCheckInterval(
        verificationStatusCheckInterval);
    this.imageTransferProperties.setVerificationStatusCheckTimeout(verificationStatusCheckTimeout);
    this.imageTransferProperties.setInitiationStatusCheckInterval(initiationStatusCheckInterval);
    this.imageTransferProperties.setInitiationStatusCheckTimeout(initiationStatusCheckTimeout);
  }

  @Override
  public UpdateFirmwareResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final String firmwareIdentification,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final byte[] imageData = this.getImageData(firmwareIdentification, device, messageMetadata);
    final ImageTransfer transfer =
        new ImageTransfer(
            conn,
            this.imageTransferProperties,
            this.getImageIdentifier(firmwareIdentification, device, imageData),
            imageData);

    try {
      this.prepare(transfer);
      this.transfer(transfer);
      this.verify(transfer);
      this.activate(transfer);
      return new UpdateFirmwareResponseDto(firmwareIdentification);
    } catch (final ImageTransferException | ProtocolAdapterException e) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_UPDATE_FAILED, e);
    } finally {
      transfer.setImageTransferEnabled(false);
    }
  }

  private boolean isMbusDevice(final DlmsDevice device) {
    return device.getMbusIdentificationNumber() != null;
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
      LOGGER.info("The current ImageTransferStatus is not INITIATED");
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

  private byte[] getImageData(
      final String firmwareIdentification,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    byte[] firmwareFile = this.firmwareFileCachingRepository.retrieve(firmwareIdentification);

    if (firmwareFile == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE);
    }

    if (this.isMbusDevice(device)) {
      firmwareFile = this.addMac(messageMetadata, device, firmwareFile);
    }

    return firmwareFile;
  }

  private byte[] addMac(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final byte[] firmwareFileByteArray)
      throws ProtocolAdapterException {
    final FirmwareFile firmwareFile = new FirmwareFile(firmwareFileByteArray);

    final int mbusDeviceIdentificationNumber = device.getMbusIdentificationNumber().intValue();
    firmwareFile.setMbusDeviceIdentificationNumber(mbusDeviceIdentificationNumber);
    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(
            messageMetadata, device.getDeviceIdentification(), firmwareFile);
    firmwareFile.setSecurityByteArray(calculatedMac);

    return firmwareFile.getByteArray();
  }

  private byte[] getImageIdentifier(
      final String firmwareIdentification, final DlmsDevice device, final byte[] imageData)
      throws ProtocolAdapterException {
    if (this.isMbusDevice(device)) {
      return new FirmwareFile(imageData).createImageIdentifierForMbusDevice();
    } else {
      return this.getImageIdentifierFromCache(firmwareIdentification);
    }
  }

  private byte[] getImageIdentifierFromCache(final String firmwareIdentification)
      throws ProtocolAdapterException {
    final String firmwareImageIdentifier =
        this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification);

    if (firmwareImageIdentifier == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_FIRMWARE_IMAGE_IDENTIFIER_NOT_AVAILABLE);
    }
    return Hex.decode(firmwareImageIdentifier);
  }

  @Override
  public String fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    return ((UpdateFirmwareRequestDto) bundleInput).getFirmwareIdentification();
  }

  @Override
  public ActionResponseDto asBundleResponse(final UpdateFirmwareResponseDto executionResult)
      throws ProtocolAdapterException {

    return executionResult;
  }
}
