/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.util.List;

import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CleanUpMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CleanUpMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CleanUpMbusDeviceByChannelCommandExecutor
        extends AbstractCommandExecutor<CleanUpMbusDeviceByChannelRequestDataDto, CleanUpMbusDeviceByChannelResponseDto> {

    @Autowired
    private DeviceChannelsHelper deviceChannelsHelper;

    public CleanUpMbusDeviceByChannelCommandExecutor() {
        super(CleanUpMbusDeviceByChannelRequestDataDto.class);
    }

    @Override
    public CleanUpMbusDeviceByChannelResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final CleanUpMbusDeviceByChannelRequestDataDto cleanUpMbusDeviceByChannelRequestDataDto) throws ProtocolAdapterException {

        Short channel = cleanUpMbusDeviceByChannelRequestDataDto.getChannel();
        
        log.debug("Clean up mbus channel {} on device {}", channel, device.getDeviceIdentification());

        final ObisCode obisCode = this.deviceChannelsHelper.getObisCode(channel);
        
        final CosemObjectAccessor mBusSetup = new CosemObjectAccessor(conn, obisCode, InterfaceClass.MBUS_CLIENT.id());

        String mbusDeviceIdentification = this.getMbusDeviceIdentification(conn, device, channel, mBusSetup);
        
        this.deviceChannelsHelper.deinstallSlave(conn, device, channel, mbusDeviceIdentification, mBusSetup);

        this.emptyEncryptionKey(conn, device, cleanUpMbusDeviceByChannelRequestDataDto, mBusSetup);
        
        this.resetMbusDeviceDetails(conn, device, channel);
        
        return new CleanUpMbusDeviceByChannelResponseDto(mbusDeviceIdentification, channel);
    }

    private String getMbusDeviceIdentification(DlmsConnectionManager conn, DlmsDevice device,
            short channel, CosemObjectAccessor mBusSetup)
            throws ProtocolAdapterException {

        log.info("Retrieving attribute values of mbus channel {} on device {}", channel,
                device.getDeviceIdentification());

        final List<GetResult> resultList = this.deviceChannelsHelper.getMBusClientAttributeValues(conn, device,
                channel);
        ChannelElementValuesDto channelElementValues = this.deviceChannelsHelper
                .makeChannelElementValues(channel, resultList);
        return channelElementValues.getIdentificationNumber();

    }

    private void emptyEncryptionKey(final DlmsConnectionManager conn, final DlmsDevice device,
            final CleanUpMbusDeviceByChannelRequestDataDto cleanUpMbusDeviceByChannelRequestDataDto, final CosemObjectAccessor mBusSetup) {
        // TODO via SetEncryptionKeyExchangeOnGMeterCommandExecutor?
    }

    private void resetMbusDeviceDetails(final DlmsConnectionManager conn,
            DlmsDevice device, final short channel) throws ProtocolAdapterException {

        MbusChannelElementsDto mbusChannelElementsDto = new MbusChannelElementsDto((short)0, "", "", "", (short)0, (short)0);
        this.deviceChannelsHelper.writeUpdatedMbus(conn,
                mbusChannelElementsDto, channel, Protocol.forDevice(device), "CleanUpMbusChannel");

    }

}
