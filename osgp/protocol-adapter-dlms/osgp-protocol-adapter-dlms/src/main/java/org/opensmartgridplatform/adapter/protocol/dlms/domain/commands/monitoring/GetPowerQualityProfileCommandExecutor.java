/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileCommandExecutor
        extends AbstractCommandExecutor<GetPowerQualityProfileRequestDataDto, GetPowerQualityProfileResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerQualityProfileCommandExecutor.class);

    @Autowired
    private GetPowerQualityProfileNoSelectiveAccessHandler getPowerQualityProfileNoSelectiveAccessHandler;

    @Autowired
    private GetPowerQualityProfileSelectiveAccessHandler getPowerQualityProfileSelectiveAccessHandler;

    public GetPowerQualityProfileCommandExecutor() {
        super(GetPowerQualityProfileRequestDataDto.class);
    }

    @Override
    public GetPowerQualityProfileResponseDto execute(DlmsConnectionManager conn, DlmsDevice device,
            GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto) throws ProtocolAdapterException {

        LOGGER.info("----- GetPowerQualityProfileCommandExecutor selective access == {} ----",
                device.isSelectiveAccessSupported());

        if (device.isSelectiveAccessSupported()) {

            LOGGER.info("Executing PQ Command Executor with Selective Access");

            return this.getPowerQualityProfileSelectiveAccessHandler
                    .handle(conn, device, getPowerQualityProfileRequestDataDto);
        } else {

            LOGGER.info("Executing PQ Command Executor without Selective Access");

            return this.getPowerQualityProfileNoSelectiveAccessHandler
                    .handle(conn, device, getPowerQualityProfileRequestDataDto);
        }
    }
}
