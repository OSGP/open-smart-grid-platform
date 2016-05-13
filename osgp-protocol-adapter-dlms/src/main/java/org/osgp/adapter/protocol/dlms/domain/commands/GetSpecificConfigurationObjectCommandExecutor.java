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
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificConfigurationObjectRequestDto;

@Component
public class GetSpecificConfigurationObjectCommandExecutor implements
CommandExecutor<SpecificConfigurationObjectRequestDto, String> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSpecificConfigurationObjectCommandExecutor.class);

    @Override
    public String execute(final ClientConnection conn, final DlmsDevice device,
            final SpecificConfigurationObjectRequestDto requestData) throws ProtocolAdapterException {

        final ObisCode obisCode = new ObisCode(requestData.getObisCode().getA(), requestData.getObisCode().getB(),
                requestData.getObisCode().getC(), requestData.getObisCode().getD(), requestData.getObisCode().getE(),
                requestData.getObisCode().getF());

        LOGGER.debug("Get specific configuration object for class id: {}, obis code: {}, attribute id: {}",
                requestData.getClassId(), obisCode, requestData.getAttribute());

        final AttributeAddress attributeAddress = new AttributeAddress(requestData.getClassId(), obisCode,
                requestData.getAttribute());

        final List<GetResult> getResultList = this.dlmsHelper.getAndCheck(conn, device,
                "Get specific configuration object for class", attributeAddress);
        final DataObject dataObject = getResultList.get(0).resultData();
        return this.dlmsHelper.getDebugInfo(dataObject);
    }

}
