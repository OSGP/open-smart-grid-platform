/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.osgp.adapter.protocol.dlms.domain.commands;

import static org.osgp.adapter.protocol.dlms.domain.commands.DeviceChannelsHelper.FIRST_CHANNEL;
import static org.osgp.adapter.protocol.dlms.domain.commands.DeviceChannelsHelper.SECOND_CHANNEL;
import static org.osgp.adapter.protocol.dlms.domain.commands.DeviceChannelsHelper.THIRD_CHANNEL;
import static org.osgp.adapter.protocol.dlms.domain.commands.DeviceChannelsHelper.FOURTH_CHANNEL;

import java.util.List;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class ScanMbusChannelsCommandExecutor extends AbstractCommandExecutor<Void, ScanMbusChannelsResponseDto> {

    @Autowired
    private DeviceChannelsHelper deviceChannelsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanMbusChannelsCommandExecutor.class);

    public ScanMbusChannelsCommandExecutor() {
        super(ScanMbusChannelsRequestDataDto.class);
    }

    @Override
    public Void fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        /*
         * ScanMbusChannelsRequestDto does not contain any values to pass on, and the
         * ScanMbusChannelsCommandExecutor takes a Void as input that is ignored.
         */
        return null;
    }

    @Override
    public ScanMbusChannelsResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Void mbusAttributesDto) throws OsgpException {
        LOGGER.debug("retrieving mbus info on e-meter");

        final List<ChannelElementValuesDto> candidateChannelElementValues = this.deviceChannelsHelper.findCandidateChannelsForDevice(conn,
                device, null);

        return new ScanMbusChannelsResponseDto(
                this.deviceChannelsHelper.findChannelElementValueForChannel(candidateChannelElementValues, FIRST_CHANNEL).getIdentificationNumber(),
                this.deviceChannelsHelper.findChannelElementValueForChannel(candidateChannelElementValues, SECOND_CHANNEL).getIdentificationNumber(),
                this.deviceChannelsHelper.findChannelElementValueForChannel(candidateChannelElementValues, THIRD_CHANNEL).getIdentificationNumber(),
                this.deviceChannelsHelper.findChannelElementValueForChannel(candidateChannelElementValues, FOURTH_CHANNEL).getIdentificationNumber()
        );
    }
}
