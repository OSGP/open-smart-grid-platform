/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetSpecificConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificConfigurationObjectDto;

@Component
public class GetSpecificConfigurationObjectCommandExecutorImpl implements GetSpecificConfigurationObjectCommandExecutor {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final String DATA_UNAVAILABLE = "DATA UNAVAILABLE";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveConfigurationObjectsCommandExecutor.class);

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final GetSpecificConfigurationObjectRequestDataDto requestData) throws ProtocolAdapterException {

        LOGGER.debug("Get specific configuration object for class id: {}, obis code: {}, attribute id: {}",
                getClassId(requestData), getObisCode(requestData), getAttr(requestData));

        final AttributeAddress attributeAddress = getAttributeAddress(requestData);

        List<GetResult> getResultList;
        try {
            getResultList = conn.get(attributeAddress);
            if (checkResult(getResultList)) {
                final DataObject dataObject = getResultList.get(0).resultData();
                return new ActionResponseDto(this.dlmsHelper.getDebugInfo(dataObject));
            } else {
                return new ActionResponseDto(DATA_UNAVAILABLE);               
            }
        } catch (IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }
    }

    private boolean checkResult(List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving administrative status.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving administrative status, got "
                    + getResultList.size());
        }

        return getResultList.get(0).resultData() != null && getResultList.get(0).resultData().isNumber();
    }

    private AttributeAddress getAttributeAddress(final GetSpecificConfigurationObjectRequestDataDto requestData) {
        return new AttributeAddress(getClassId(requestData), getObisCode(requestData), getAttr(requestData));
    }

    private int getClassId(final GetSpecificConfigurationObjectRequestDataDto requestData) {
        return requestData.getSpecificConfigurationObject().getClassId();
    }

    private int getAttr(final GetSpecificConfigurationObjectRequestDataDto requestData) {
        return requestData.getSpecificConfigurationObject().getAttribute();
    }

    private ObisCode getObisCode(final GetSpecificConfigurationObjectRequestDataDto requestData) {
        final SpecificConfigurationObjectDto specConfig = requestData.getSpecificConfigurationObject();
        final ObisCodeValuesDto obisDto = specConfig.getObisCode();
        return new ObisCode((int) obisDto.getA(), (int) obisDto.getB(), (int) obisDto.getC(), (int) obisDto.getD(),
                (int) obisDto.getE(), (int) obisDto.getF());
    }
}
