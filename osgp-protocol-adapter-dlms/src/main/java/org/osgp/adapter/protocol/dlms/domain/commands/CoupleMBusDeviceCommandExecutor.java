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
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Component
public class CoupleMBusDeviceCommandExecutor extends AbstractCommandExecutor<MbusChannelElementsDto, String> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(CoupleMBusDeviceCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.24.1.0.255");
    // private static final int ATTRIBUTE_ID = 2;

    public CoupleMBusDeviceCommandExecutor() {
        super(SpecificAttributeValueRequestDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final String executionResult) throws ProtocolAdapterException {
        return new ActionResponseDto(executionResult);
    }

    @Override
    public String execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto requestData) throws ProtocolAdapterException, FunctionalException {

        final AttributeAddress[] attrAddresses = new AttributeAddress[4];
        for (int i = 0; i < attrAddresses.length; i++) {
            attrAddresses[i] = new AttributeAddress(CLASS_ID, OBIS_CODE, 6 + i);
        }

        conn.getDlmsMessageListener().setDescription(
                "CoupleMBusDevice, retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(attrAddresses));

        final List<GetResult> resultList = this.dlmsHelper.getWithList(conn, device, attrAddresses);
        return resultList.toString();
    }

}
