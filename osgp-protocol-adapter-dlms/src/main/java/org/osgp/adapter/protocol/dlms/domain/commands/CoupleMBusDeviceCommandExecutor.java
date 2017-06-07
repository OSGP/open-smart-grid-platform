/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.MbusClientAttribute;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDtoBuilder;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;

@Component
public class CoupleMBusDeviceCommandExecutor
        extends AbstractCommandExecutor<MbusChannelElementsDto, MbusChannelElementsResponseDto> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMBusDeviceCommandExecutor.class);

    private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
    private static final String OBIS_CODE_TEMPLATE = "0.%d.24.1.0.255";

    /**
     * We need to collect data for NR_OF_ATTRIBUTES attributes, starting from
     * attribute-ID: 5
     */
    private static final int[] ATTRIBUTE_IDS = new int[] { MbusClientAttribute.PRIMARY_ADDRESS.attributeId(),
            MbusClientAttribute.IDENTIFICATION_NUMBER.attributeId(), MbusClientAttribute.MANUFACTURER_ID.attributeId(),
            MbusClientAttribute.VERSION.attributeId(), MbusClientAttribute.DEVICE_TYPE.attributeId() };

    /**
     * and we start at channel 1 until channel 4
     */
    private static final int FIRST_CHANNEL = 1;
    private static final int NR_OF_CHANNELS = 4;

    public CoupleMBusDeviceCommandExecutor() {
        super(SpecificAttributeValueRequestDto.class);
    }

    @Override
    public MbusChannelElementsResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto requestDto) throws ProtocolAdapterException {

        LOGGER.debug("retrieving mbus info on e-meter");
        final MbusChannelElementsResponseDtoBuilder builder = new MbusChannelElementsResponseDtoBuilder()
                .withMbusChannelElementsDto(requestDto);
        final short[] mbusChannelMatchScores = new short[FIRST_CHANNEL + NR_OF_CHANNELS];

        for (short channel = FIRST_CHANNEL; channel < FIRST_CHANNEL + NR_OF_CHANNELS; channel++) {
            final List<GetResult> resultList = this.getResultList(conn, device, channel);
            final ChannelElementValuesDto channelValues = this.makeChannelElementValues(channel, resultList);
            builder.withAddChannelValues(channelValues);
            mbusChannelMatchScores[channel - 1] = this.mbusChannelMatchScore(channelValues, requestDto);
        }

        final short bestChannel = this.getBestChannelScore(mbusChannelMatchScores);
        if (bestChannel > 0) {
            builder.withChannel(bestChannel);
        }
        return builder.build();
    }

    private short getBestChannelScore(final short[] mbusChannelMatchScores) {
        short bestChannelScore = 0;
        for (short channel = FIRST_CHANNEL; channel < FIRST_CHANNEL + NR_OF_CHANNELS; channel++) {
            if (mbusChannelMatchScores[channel] > bestChannelScore) {
                bestChannelScore = mbusChannelMatchScores[channel];
            }
        }
        return bestChannelScore;
    }

    private List<GetResult> getResultList(final DlmsConnectionHolder conn, final DlmsDevice device, final short channel)
            throws ProtocolAdapterException {
        final AttributeAddress[] attrAddresses = this.makeAttributeAddresses(channel);
        conn.getDlmsMessageListener().setDescription("CoupleMBusDevice, retrieve M-Bus client setup attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attrAddresses));
        return this.dlmsHelper.getWithList(conn, device, attrAddresses);
    }

    private AttributeAddress[] makeAttributeAddresses(final int channel) {
        final AttributeAddress[] attrAddresses = new AttributeAddress[ATTRIBUTE_IDS.length];
        final ObisCode obiscode = new ObisCode(String.format(OBIS_CODE_TEMPLATE, channel));
        for (int i = 0; i < attrAddresses.length; i++) {
            attrAddresses[i] = new AttributeAddress(CLASS_ID, obiscode, ATTRIBUTE_IDS[i]);
        }
        return attrAddresses;
    }

    private short mbusChannelMatchScore(final ChannelElementValuesDto channelValues,
            final MbusChannelElementsDto requestData) {
        return FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
    }

    private ChannelElementValuesDto makeChannelElementValues(final short channel, final List<GetResult> resultList)
            throws ProtocolAdapterException {
        final short primaryAddress = this.readShort(resultList, 0, "primaryAddress");
        final int identificationNumber = this.readInt(resultList, 1, "identificationNumber");
        final int manufacturerIdentification = this.readInt(resultList, 2, "manufacturerIdentification");
        final short version = this.readShort(resultList, 3, "version");
        final short deviceTypeIdentification = this.readShort(resultList, 4, "deviceTypeIdentification");
        return new ChannelElementValuesDto(channel, primaryAddress, identificationNumber, manufacturerIdentification,
                version, deviceTypeIdentification);
    }

    private int readInt(final List<GetResult> resultList, final int index, final String description)
            throws ProtocolAdapterException {
        final Integer value = this.dlmsHelper.readInt(resultList.get(index), description);
        return value == null ? 0 : value;
    }

    private short readShort(final List<GetResult> resultList, final int index, final String description)
            throws ProtocolAdapterException {
        final Short value = this.dlmsHelper.readShort(resultList.get(index), description);
        return value == null ? 0 : value;
    }

}
