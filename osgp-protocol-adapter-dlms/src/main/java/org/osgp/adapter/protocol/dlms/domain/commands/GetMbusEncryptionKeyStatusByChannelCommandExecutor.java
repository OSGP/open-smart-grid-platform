/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Component
public class GetMbusEncryptionKeyStatusByChannelCommandExecutor extends
        AbstractCommandExecutor<GetMbusEncryptionKeyStatusByChannelRequestDataDto, GetMbusEncryptionKeyStatusByChannelResponseDto> {

    @Autowired
    private GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor;

    @Autowired
    private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

    public GetMbusEncryptionKeyStatusByChannelCommandExecutor() {
        super(GetMbusEncryptionKeyStatusByChannelRequestDataDto.class);
    }

    @Override
    public GetMbusEncryptionKeyStatusByChannelResponseDto execute(final DlmsConnectionHolder conn,
            final DlmsDevice device, final GetMbusEncryptionKeyStatusByChannelRequestDataDto request)
            throws ProtocolAdapterException, FunctionalException {

        final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest = new GetMBusDeviceOnChannelRequestDataDto(
                device.getDeviceIdentification(), request.getChannel());
        final ChannelElementValuesDto channelElementValues = this.getMBusDeviceOnChannelCommandExecutor.execute(conn,
                device, mbusDeviceOnChannelRequest);

        if (!channelElementValues.hasChannel() || !channelElementValues.hasDeviceTypeIdentification()
                || !channelElementValues.hasManufacturerIdentification()) {
            throw new FunctionalException(FunctionalExceptionType.NO_DEVICE_FOUND_ON_CHANNEL,
                    ComponentType.DOMAIN_SMART_METERING);
        }

        final EncryptionKeyStatusTypeDto encryptionKeyStatusType = this.getMbusEncryptionKeyStatusCommandExecutor
                .getEncryptionKeyStatusTypeDto(request.getChannel(), conn);
        return new GetMbusEncryptionKeyStatusByChannelResponseDto(device.getDeviceIdentification(),
                encryptionKeyStatusType, request.getChannel());
    }

}
