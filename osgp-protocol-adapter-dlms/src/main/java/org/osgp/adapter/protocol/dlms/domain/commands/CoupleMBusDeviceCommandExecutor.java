/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.MbusClientAttribute;
import org.osgp.adapter.protocol.dlms.domain.commands.mbus.IdentificationNumber;
import org.osgp.adapter.protocol.dlms.domain.commands.mbus.ManufacturerId;
import org.osgp.adapter.protocol.dlms.domain.commands.utils.FindMatchingChannelHelper;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;

@Component
public class CoupleMBusDeviceCommandExecutor
        extends AbstractCommandExecutor<MbusChannelElementsDto, MbusChannelElementsResponseDto> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMBusDeviceCommandExecutor.class);

    private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
    /**
     * The ObisCode for the M-Bus Client Setup exists for a number of channels.
     * DSMR specifies these M-Bus Client Setup channels as values from 1..4.
     */
    private static final String OBIS_CODE_TEMPLATE = "0.%d.24.1.0.255";
    private static final int FIRST_CHANNEL = 1;
    private static final int NR_OF_CHANNELS = 4;

    private static final int NUMBER_OF_ATTRIBUTES_MBUS_CLIENT = 5;
    private static final int INDEX_PRIMARY_ADDRESS = 0;
    private static final int INDEX_IDENTIFICATION_NUMBER = 1;
    private static final int INDEX_MANUFACTURER_ID = 2;
    private static final int INDEX_VERSION = 3;
    private static final int INDEX_DEVICE_TYPE = 4;

    public CoupleMBusDeviceCommandExecutor() {
        super(MbusChannelElementsDto.class);
    }

    @Override
    public MbusChannelElementsResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto requestDto) throws ProtocolAdapterException {

        LOGGER.debug("retrieving mbus info on e-meter");
        ChannelElementValuesDto bestMatch = null;
        final List<ChannelElementValuesDto> channelElements = new ArrayList<>();
        for (short channel = FIRST_CHANNEL; channel < FIRST_CHANNEL + NR_OF_CHANNELS; channel++) {
            final List<GetResult> resultList = this.getMBusClientAttributeValues(conn, device, channel);
            final ChannelElementValuesDto channelValues = this.makeChannelElementValues(channel, resultList);
            channelElements.add(channelValues);
            if (FindMatchingChannelHelper.matches(requestDto, channelValues)) {
                /*
                 * A complete match for all attributes from the request has been
                 * found. Stop retrieving M-Bus Client Setup attributes for
                 * other channels.
                 */
                bestMatch = channelValues;
                break;
            }
        }
        if (bestMatch == null) {
            /*
             * A complete match for all attributes from the request has not been
             * found. Select the best partial match that has no conflicting
             * attribute values.
             */
            bestMatch = FindMatchingChannelHelper.bestMatch(requestDto, channelElements);
        }
        return new MbusChannelElementsResponseDto(requestDto, bestMatch == null ? null : bestMatch.getChannel(),
                channelElements);
    }

    private List<GetResult> getMBusClientAttributeValues(final DlmsConnectionHolder conn, final DlmsDevice device,
            final short channel) throws ProtocolAdapterException {
        final AttributeAddress[] attrAddresses = this.makeAttributeAddresses(channel);
        conn.getDlmsMessageListener().setDescription("CoupleMBusDevice, retrieve M-Bus client setup attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attrAddresses));
        return this.dlmsHelper.getWithList(conn, device, attrAddresses);
    }

    private AttributeAddress[] makeAttributeAddresses(final int channel) {
        final AttributeAddress[] attrAddresses = new AttributeAddress[NUMBER_OF_ATTRIBUTES_MBUS_CLIENT];
        final ObisCode obiscode = new ObisCode(String.format(OBIS_CODE_TEMPLATE, channel));
        attrAddresses[INDEX_PRIMARY_ADDRESS] = new AttributeAddress(CLASS_ID, obiscode,
                MbusClientAttribute.PRIMARY_ADDRESS.attributeId());
        attrAddresses[INDEX_IDENTIFICATION_NUMBER] = new AttributeAddress(CLASS_ID, obiscode,
                MbusClientAttribute.IDENTIFICATION_NUMBER.attributeId());
        attrAddresses[INDEX_MANUFACTURER_ID] = new AttributeAddress(CLASS_ID, obiscode,
                MbusClientAttribute.MANUFACTURER_ID.attributeId());
        attrAddresses[INDEX_VERSION] = new AttributeAddress(CLASS_ID, obiscode,
                MbusClientAttribute.VERSION.attributeId());
        attrAddresses[INDEX_DEVICE_TYPE] = new AttributeAddress(CLASS_ID, obiscode,
                MbusClientAttribute.DEVICE_TYPE.attributeId());
        return attrAddresses;
    }

    private ChannelElementValuesDto makeChannelElementValues(final short channel, final List<GetResult> resultList)
            throws ProtocolAdapterException {
        final short primaryAddress = this.readShort(resultList, INDEX_PRIMARY_ADDRESS, "primaryAddress");
        final String identificationNumber = this.readIdentificationNumber(resultList, INDEX_IDENTIFICATION_NUMBER,
                "identificationNumber");
        final String manufacturerIdentification = this.readManufacturerIdentification(resultList, INDEX_MANUFACTURER_ID,
                "manufacturerIdentification");
        final short version = this.readShort(resultList, INDEX_VERSION, "version");
        final short deviceTypeIdentification = this.readShort(resultList, INDEX_DEVICE_TYPE,
                "deviceTypeIdentification");
        return new ChannelElementValuesDto(channel, primaryAddress, identificationNumber, manufacturerIdentification,
                version, deviceTypeIdentification);
    }

    private String readIdentificationNumber(final List<GetResult> resultList, final int index, final String description)
            throws ProtocolAdapterException {

        final Long identification = this.dlmsHelper.readLong(resultList.get(index), description);
        return IdentificationNumber.fromIdentification(identification).getLast8Digits();
    }

    private String readManufacturerIdentification(final List<GetResult> resultList, final int index,
            final String description) throws ProtocolAdapterException {

        final int manufacturerId = this.readInt(resultList, index, description);
        return ManufacturerId.fromId(manufacturerId).getIdentification();
    }

    private int readInt(final List<GetResult> resultList, final int index, final String description)
            throws ProtocolAdapterException {
        final Integer value = this.dlmsHelper.readInteger(resultList.get(index), description);
        return value == null ? 0 : value;
    }

    private short readShort(final List<GetResult> resultList, final int index, final String description)
            throws ProtocolAdapterException {
        final Short value = this.dlmsHelper.readShort(resultList.get(index), description);
        return value == null ? 0 : value;
    }

}
