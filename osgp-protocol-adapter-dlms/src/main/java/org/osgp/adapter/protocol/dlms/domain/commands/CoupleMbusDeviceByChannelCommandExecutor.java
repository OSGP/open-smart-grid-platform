/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.GetResult;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;

@Component
public class CoupleMbusDeviceByChannelCommandExecutor
        extends AbstractCommandExecutor<CoupleMbusDeviceByChannelRequestDataDto, CoupleMbusDeviceByChannelResponseDto> {

    @Autowired
    private CoupleMBusDeviceCommandExecutor coupleMBusDeviceCommandExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMbusDeviceByChannelCommandExecutor.class);

    public CoupleMbusDeviceByChannelCommandExecutor() {
        super(CoupleMbusDeviceByChannelRequestDataDto.class);
    }

    @Override
    public CoupleMbusDeviceByChannelResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final CoupleMbusDeviceByChannelRequestDataDto requestDto) throws ProtocolAdapterException {

        LOGGER.info("Retrieving values for mbus channel {} on device {}", requestDto.getChannel(),
                device.getDeviceIdentification());
        final List<GetResult> resultList = this.coupleMBusDeviceCommandExecutor.getMBusClientAttributeValues(conn,
                device, requestDto.getChannel());

        return new CoupleMbusDeviceByChannelResponseDto(
                this.coupleMBusDeviceCommandExecutor.makeChannelElementValues(requestDto.getChannel(), resultList));
    }

}
