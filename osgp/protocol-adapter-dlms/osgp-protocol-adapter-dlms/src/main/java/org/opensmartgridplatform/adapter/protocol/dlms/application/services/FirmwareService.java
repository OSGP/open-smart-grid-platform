/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.GetFirmwareVersionsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.UpdateFirmwareCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareImageIdentifierCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsFirmwareService")
public class FirmwareService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareService.class);

  private static final String EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE =
      "Firmware file %s is not available.";

  @Autowired private FirmwareFileCachingRepository firmwareRepository;

  @Autowired private FirmwareImageIdentifierCachingRepository imageIdentifierRepository;

  @Autowired private GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;

  @Autowired private UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor;

  public List<FirmwareVersionDto> getFirmwareVersions(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getFirmwareVersionsCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public UpdateFirmwareResponseDto updateFirmware(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    LOGGER.info(
        "Updating firmware of device {} to firmware with identification {}",
        device,
        updateFirmwareRequestDto.getFirmwareIdentification());

    return this.executeFirmwareUpdate(conn, device, updateFirmwareRequestDto, messageMetadata);
  }

  public UpdateFirmwareResponseDto updateFirmware(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final FirmwareFileDto firmwareFileDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    LOGGER.info(
        "Updating firmware of device {} to firmware with identification {} using included firmware file",
        device,
        firmwareFileDto.getFirmwareIdentification());

    if (ArrayUtils.isEmpty(firmwareFileDto.getFirmwareFile())) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE,
              firmwareFileDto.getFirmwareIdentification()));
    }
    this.firmwareRepository.store(
        firmwareFileDto.getFirmwareIdentification(), firmwareFileDto.getFirmwareFile());
    if (firmwareFileDto.getImageIdentifier() != null) {
      this.imageIdentifierRepository.store(
          firmwareFileDto.getFirmwareIdentification(), firmwareFileDto.getImageIdentifier());
    }

    return this.executeFirmwareUpdate(
        conn,
        device,
        new UpdateFirmwareRequestDto(
            firmwareFileDto.getFirmwareIdentification(), firmwareFileDto.getDeviceIdentification()),
        messageMetadata);
  }

  public boolean isFirmwareFileAvailable(final String firmwareIdentification) {
    return this.firmwareRepository.isAvailable(firmwareIdentification);
  }

  private UpdateFirmwareResponseDto executeFirmwareUpdate(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    if (this.firmwareRepository.isAvailable(updateFirmwareRequestDto.getFirmwareIdentification())) {
      return this.updateFirmwareCommandExecutor.execute(
          conn, device, updateFirmwareRequestDto, messageMetadata);
    } else {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE,
              updateFirmwareRequestDto.getFirmwareIdentification()));
    }
  }
}
