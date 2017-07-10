/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.List;

import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.UpdateFirmwareCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.FirmwareFileDto;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

@Service(value = "dlmsFirmwareService")
public class FirmwareService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareService.class);

    @Autowired
    private FirmwareFileCachingRepository firmwareRepository;

    @Autowired
    private GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;

    @Autowired
    private UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor;

    public List<FirmwareVersionDto> getFirmwareVersions(final DlmsConnectionHolder conn, final DlmsDevice device)
            throws ProtocolAdapterException {

        return this.getFirmwareVersionsCommandExecutor.execute(conn, device, null);
    }

    public List<FirmwareVersionDto> updateFirmware(final DlmsConnectionHolder conn, final DlmsDevice device,
            final String firmwareIdentification) throws ProtocolAdapterException {
        LOGGER.info("Updating firmware of device {} to firmware with identification {}", device,
                firmwareIdentification);

        return this.executeFirmwareUpdate(conn, device, firmwareIdentification);
    }

    public List<FirmwareVersionDto> updateFirmware(final DlmsConnectionHolder conn, final DlmsDevice device,
            final FirmwareFileDto firmwareFileDto) throws ProtocolAdapterException {
        LOGGER.info("Updating firmware of device {} to firmware with identification {} using included firmware file",
                device, firmwareFileDto.getFirmwareIdentification());

        this.firmwareRepository.store(firmwareFileDto.getFirmwareIdentification(), firmwareFileDto.getFirmwareFile());

        return this.executeFirmwareUpdate(conn, device, firmwareFileDto.getFirmwareIdentification());
    }

    public boolean isFirmwareFileAvailable(final String firmwareIdentification) {
        return this.firmwareRepository.isAvailable(firmwareIdentification);
    }

    private List<FirmwareVersionDto> executeFirmwareUpdate(final DlmsConnectionHolder conn, final DlmsDevice device,
            final String firmwareIdentification) throws ProtocolAdapterException {
        if (this.firmwareRepository.isAvailable(firmwareIdentification)) {
            return this.updateFirmwareCommandExecutor.execute(conn, device, firmwareIdentification);
        } else {
            throw new ProtocolAdapterException("Firmware Unavailable");
        }
    }
}
