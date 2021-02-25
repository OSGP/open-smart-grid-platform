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

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CleanUpMbusChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CleanUpMbusChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CleanUpMBusChannelCommandExecutor
        extends AbstractCommandExecutor<CleanUpMbusChannelDto, CleanUpMbusChannelResponseDto> {

    @Autowired
    private DeviceChannelsHelper deviceChannelsHelper;

    private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
    private static final int SLAVE_DEINSTALL_METHOD_ID = MBusClientMethod.SLAVE_DEINSTALL.getMethodId();
    private static final int SET_ENCRYPTION_KEY_METHOD_ID = MBusClientMethod.SET_ENCRYPTION_KEY.getMethodId();
    private static final int IDENTIFICATION_NUMBER_ATTRIBUTE_ID = MbusClientAttribute.IDENTIFICATION_NUMBER.attributeId();
    /**
     * The ObisCode for the M-Bus Client Setup exists for a number of channels.
     * DSMR specifies these M-Bus Client Setup channels as values from 1..4.
     */
    private static final String OBIS_CODE_TEMPLATE = "0.%d.24.1.0.255";

    public CleanUpMBusChannelCommandExecutor() {
        super(CleanUpMbusChannelDto.class);
    }

    @Override
    public CleanUpMbusChannelResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final CleanUpMbusChannelDto cleanUpMbusChannelDto) throws ProtocolAdapterException {

        log.debug("Clean up mbus channel {} on device {}", cleanUpMbusChannelDto.getChannel(),
                device.getDeviceIdentification());

        final CosemObjectAccessor mBusSetup = new CosemObjectAccessor(conn, this.getObisCode(cleanUpMbusChannelDto),
                CLASS_ID);

        this.deinstallSlave(conn, device, cleanUpMbusChannelDto, mBusSetup);

        this.emptyEncryptionKey(conn, device, cleanUpMbusChannelDto, mBusSetup);
        
        String mbusDeviceIdentification = this.getMbusDeviceIdentification(conn, device, cleanUpMbusChannelDto, mBusSetup);
        
        this.resetMbusDeviceDetails(conn, device, cleanUpMbusChannelDto);
        
        return new CleanUpMbusChannelResponseDto(mbusDeviceIdentification, cleanUpMbusChannelDto.getChannel());
    }

    private String getMbusDeviceIdentification(DlmsConnectionManager conn, DlmsDevice device,
            CleanUpMbusChannelDto cleanUpMbusChannelDto, CosemObjectAccessor mBusSetup)
            throws ProtocolAdapterException {

        log.info("Retrieving attribute values of mbus channel {} on device {}", cleanUpMbusChannelDto.getChannel(),
                device.getDeviceIdentification());

        final List<GetResult> resultList = this.deviceChannelsHelper.getMBusClientAttributeValues(conn, device,
                cleanUpMbusChannelDto.getChannel());
        ChannelElementValuesDto channelElementValues = this.deviceChannelsHelper
                .makeChannelElementValues(cleanUpMbusChannelDto.getChannel(), resultList);
        return channelElementValues.getIdentificationNumber();

    }

    private void emptyEncryptionKey(final DlmsConnectionManager conn, final DlmsDevice device,
            final CleanUpMbusChannelDto cleanUpMbusChannelDto, final CosemObjectAccessor mBusSetup) {
        // TODO via SetEncryptionKeyExchangeOnGMeterCommandExecutor?
    }

    private void deinstallSlave(final DlmsConnectionManager conn, final DlmsDevice device,
            final CleanUpMbusChannelDto cleanUpMbusChannelDto, final CosemObjectAccessor mBusSetup)
            throws ProtocolAdapterException {
        // in blue book version 10, the parameter is of type integer
        DataObject parameter = DataObject.newInteger8Data((byte) 0);
        conn.getDlmsMessageListener().setDescription("Call slave deinstall method");
        MethodResultCode slaveDeinstall = mBusSetup.callMethod(Method.SLAVE_DEINSTALL, parameter);
        if (slaveDeinstall == MethodResultCode.TYPE_UNMATCHED) {
            // in blue book version 12, the parameter is of type unsigned, we
            // will try again with that type
            parameter = DataObject.newUInteger8Data((byte) 0);
            slaveDeinstall = mBusSetup.callMethod(Method.SLAVE_DEINSTALL, parameter);
        }
        if (slaveDeinstall != MethodResultCode.SUCCESS) {
            log.warn("Slave deinstall was not successfull on device {} for channel {}",
                    device.getDeviceIdentification(), cleanUpMbusChannelDto.getChannel());
        }
    }

    private ObisCode getObisCode(final CleanUpMbusChannelDto cleanUpMbusChannelDto) {
        return new ObisCode(String.format(OBIS_CODE_TEMPLATE, cleanUpMbusChannelDto.getChannel()));
    }

    private void resetMbusDeviceDetails(final DlmsConnectionManager conn,
            DlmsDevice device, final CleanUpMbusChannelDto cleanUpMbusChannelDto) throws ProtocolAdapterException {

        MbusChannelElementsDto mbusChannelElementsDto = new MbusChannelElementsDto((short)0, "", "", "", (short)0, (short)0);
        this.deviceChannelsHelper.writeUpdatedMbus(conn,
                mbusChannelElementsDto, cleanUpMbusChannelDto.getChannel(), Protocol.forDevice(device));

    }

    private enum Method implements CosemObjectMethod {
        SLAVE_DEINSTALL(2),
        SET_ENCRYPTION_KEY(7);

        private final int methodId;

        Method(final int methodId) {
            this.methodId = methodId;
        }

        @Override
        public int getValue() {
            return this.methodId;
        }
    }

}
