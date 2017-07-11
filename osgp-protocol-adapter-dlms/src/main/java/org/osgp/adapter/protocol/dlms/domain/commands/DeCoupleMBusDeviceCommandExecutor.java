/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.MbusClientAttribute;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;

@Component
public class DeCoupleMBusDeviceCommandExecutor
        extends AbstractCommandExecutor<DecoupleMbusDeviceResponseDto, DecoupleMbusDeviceResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeCoupleMBusDeviceCommandExecutor.class);

    private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
    /**
     * The ObisCode for the M-Bus Client Setup exists for a number of channels.
     * DSMR specifies these M-Bus Client Setup channels as values from 1..4.
     */
    private static final String OBIS_CODE_TEMPLATE = "0.%d.24.1.0.255";

    private static final DataObject UINT_8_ZERO = DataObject.newUInteger8Data((short) 0);
    private static final DataObject UINT_16_ZERO = DataObject.newUInteger16Data(0);
    private static final DataObject UINT_32_ZERO = DataObject.newUInteger32Data(0L);

    public DeCoupleMBusDeviceCommandExecutor() {
        super(DecoupleMbusDeviceResponseDto.class);
    }

    @Override
    public DecoupleMbusDeviceResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final DecoupleMbusDeviceResponseDto decoupleMbusDto) throws ProtocolAdapterException {

        LOGGER.debug("DeCouple mbus device from gateway device");

        return this.writeUpdatedMbus(conn, decoupleMbusDto);
    }

    private DecoupleMbusDeviceResponseDto writeUpdatedMbus(final DlmsConnectionHolder conn,
            final DecoupleMbusDeviceResponseDto deCoupleMbusDeviceResponseDto) throws ProtocolAdapterException {

        final DataObjectAttrExecutors dataObjectExecutors = new DataObjectAttrExecutors("DeCoupleMBusDevice")
                .addExecutor(this.getMbusAttributeExecutor(deCoupleMbusDeviceResponseDto,
                        MbusClientAttribute.PRIMARY_ADDRESS.attributeId(), UINT_8_ZERO))
                .addExecutor(this.getMbusAttributeExecutor(deCoupleMbusDeviceResponseDto,
                        MbusClientAttribute.IDENTIFICATION_NUMBER.attributeId(), UINT_32_ZERO))
                .addExecutor(this.getMbusAttributeExecutor(deCoupleMbusDeviceResponseDto,
                        MbusClientAttribute.MANUFACTURER_ID.attributeId(), UINT_16_ZERO))
                .addExecutor(this.getMbusAttributeExecutor(deCoupleMbusDeviceResponseDto,
                        MbusClientAttribute.VERSION.attributeId(), UINT_8_ZERO))
                .addExecutor(this.getMbusAttributeExecutor(deCoupleMbusDeviceResponseDto,
                        MbusClientAttribute.DEVICE_TYPE.attributeId(), UINT_8_ZERO));

        conn.getDlmsMessageListener()
                .setDescription("Write updated MBus attributes to channel " + deCoupleMbusDeviceResponseDto.getChannel()
                        + ", set attributes: " + dataObjectExecutors.describeAttributes());

        dataObjectExecutors.execute(conn);

        LOGGER.info("Finished decoupling the mbus device from the gateway device");

        return deCoupleMbusDeviceResponseDto;
    }

    private DataObjectAttrExecutor getMbusAttributeExecutor(
            final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto, final int attributeId,
            final DataObject value) {
        final ObisCode obiscode = new ObisCode(
                String.format(OBIS_CODE_TEMPLATE, decoupleMbusDeviceResponseDto.getChannel()));
        final AttributeAddress attributeAddress = new AttributeAddress(CLASS_ID, obiscode, attributeId);

        return new DataObjectAttrExecutor(attributeAddress.toString(), attributeAddress, value, CLASS_ID, obiscode,
                attributeId);
    }
}
