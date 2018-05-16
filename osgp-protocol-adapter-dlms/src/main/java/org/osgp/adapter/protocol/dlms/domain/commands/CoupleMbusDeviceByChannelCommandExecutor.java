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
    private DeviceChannelsHelper deviceChannelsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMbusDeviceByChannelCommandExecutor.class);

    public CoupleMbusDeviceByChannelCommandExecutor() {
        super(CoupleMbusDeviceByChannelRequestDataDto.class);
    }

    @Override
    public CoupleMbusDeviceByChannelResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final CoupleMbusDeviceByChannelRequestDataDto requestDto) throws ProtocolAdapterException {

        LOGGER.info("Retrieving values for mbus channel {} on device {}", requestDto.getChannel(),
                device.getDeviceIdentification());
        final List<GetResult> resultList = this.deviceChannelsHelper.getMBusClientAttributeValues(conn,
                device, requestDto.getChannel());

        /*
         * Couple M-Bus device by channel is created to couple the M-Bus device
         * in the platform based on a new M-Bus device discovered alarm for a
         * particular channel. As such there is no write action to the M-Bus
         * Client Setup involved, since the platform depends on the attributes
         * on the gateway device to be able to determine which M-Bus device was
         * actually involved when the alarm was triggered for the channel from
         * the request.
         */
        return new CoupleMbusDeviceByChannelResponseDto(
                this.deviceChannelsHelper.makeChannelElementValues(requestDto.getChannel(), resultList));
    }

}
