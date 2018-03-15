/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class SetDeviceLifecycleStatusByChannelCommandExecutor extends
        AbstractCommandExecutor<SetDeviceLifecycleStatusByChannelRequestDataDto, SetDeviceLifecycleStatusByChannelResponseDto> {

    @Autowired
    private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    public SetDeviceLifecycleStatusByChannelCommandExecutor() {
        super(SetDeviceLifecycleStatusByChannelRequestDataDto.class);
    }

    @Override
    public SetDeviceLifecycleStatusByChannelResponseDto execute(final DlmsConnectionHolder conn,
            final DlmsDevice gatewayDevice, final SetDeviceLifecycleStatusByChannelRequestDataDto request)
            throws OsgpException {

        final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest = new GetMBusDeviceOnChannelRequestDataDto(
                gatewayDevice.getDeviceIdentification(), request.getChannel());
        final ChannelElementValuesDto channelElementValues = this.getMBusDeviceOnChannelCommandExecutor.execute(conn,
                gatewayDevice, mbusDeviceOnChannelRequest);

        if (!channelElementValues.hasChannel() || !channelElementValues.hasDeviceTypeIdentification()
                || !channelElementValues.hasManufacturerIdentification()) {
            throw new FunctionalException(FunctionalExceptionType.NO_DEVICE_FOUND_ON_CHANNEL,
                    ComponentType.DOMAIN_SMART_METERING);
        }

        final DlmsDevice mbusDevice = this.dlmsDeviceRepository
                .findByMbusIdentificationNumberAndMbusManufacturerIdentification(
                        Long.valueOf(channelElementValues.getIdentificationNumber()),
                        channelElementValues.getManufacturerIdentification());

        return new SetDeviceLifecycleStatusByChannelResponseDto(gatewayDevice.getDeviceIdentification(),
                request.getChannel(), mbusDevice.getDeviceIdentification(), request.getDeviceLifecycleStatus());
    }
}