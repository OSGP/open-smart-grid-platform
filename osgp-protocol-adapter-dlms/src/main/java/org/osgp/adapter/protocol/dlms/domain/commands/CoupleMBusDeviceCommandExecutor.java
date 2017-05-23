/**
 * Copyright 2016 Smart Society Services B.V.
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
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Component
public class CoupleMBusDeviceCommandExecutor
        extends AbstractCommandExecutor<MbusChannelElementsDto, MbusChannelElementsResponseDto> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMBusDeviceCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final String OBIS_CODE_TEMPLATE = "0.%d.24.1.0.255";

    private static final int NR_OF_ATTRIBUTES = 5;
    // we need to collect data for NR_OF_ATTRIBUTES attribites, starting from
    // attr-id: 6
    private static final int START_ATTRIBUTE = 5;
    // and we start at channel 1 until channel 4
    private static final int FIRST_CHANNEL = 1;
    private static final int NR_OF_CHANNELS = 4;

    public CoupleMBusDeviceCommandExecutor() {
        super(SpecificAttributeValueRequestDto.class);
    }

    @Override
    public MbusChannelElementsResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto requestDto) throws ProtocolAdapterException, FunctionalException {

        LOGGER.debug("retrieving mbus info on e-meter");
        final MbusChannelElementsResponseDto responseDto = new MbusChannelElementsResponseDto(requestDto);

        for (int c = FIRST_CHANNEL; c < FIRST_CHANNEL + NR_OF_CHANNELS; c++) {
            final AttributeAddress[] attrAddresses = this.makeAttributeAddresses(c);
            conn.getDlmsMessageListener().setDescription("CoupleMBusDevice, retrieve attribute: "
                    + JdlmsObjectToStringUtil.describeAttributes(attrAddresses));
            final List<GetResult> resultList = this.dlmsHelper.getWithList(conn, device, attrAddresses);
            final ChannelElementValues channelValues = this.makeChannelElementValues(c, resultList);
            responseDto.addChannelElements(channelValues);
            if (this.mbusChannelMatches(c, channelValues, requestDto)) {
                responseDto.setChannel(c);
                break;
            }
        }

        return responseDto;
    }

    private AttributeAddress[] makeAttributeAddresses(final int channel) {
        final AttributeAddress[] attrAddresses = new AttributeAddress[NR_OF_ATTRIBUTES];
        final ObisCode obiscode = new ObisCode(String.format(OBIS_CODE_TEMPLATE, channel));
        for (int i = 0; i < attrAddresses.length; i++) {
            attrAddresses[i] = new AttributeAddress(CLASS_ID, obiscode, START_ATTRIBUTE + i);
        }
        return attrAddresses;
    }

    private boolean mbusChannelMatches(final int channel, final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {
        return (channelValues.getPrimaryAddress() > 0); // TODO-JRB
    }

    private ChannelElementValues makeChannelElementValues(final int channel, final List<GetResult> resultList) {
        final short attr5 = Short.parseShort(this.getResultDataValue(resultList, 0));
        final int attr6 = Integer.parseInt(this.getResultDataValue(resultList, 1));
        final int attr7 = Integer.parseInt(this.getResultDataValue(resultList, 2));
        final short attr8 = Short.parseShort(this.getResultDataValue(resultList, 3));
        final short attr9 = Short.parseShort(this.getResultDataValue(resultList, 4));
        return new ChannelElementValues(channel, attr5, attr6, attr7, attr8, attr9);
    }

    private String getResultDataValue(final List<GetResult> resultList, final int index) {
        final DataObject data = resultList.get(index).getResultData();
        if (data != null && data.getRawValue() != null) {
            return data.getRawValue().toString();
        } else {
            return "0";
        }
    }
}
