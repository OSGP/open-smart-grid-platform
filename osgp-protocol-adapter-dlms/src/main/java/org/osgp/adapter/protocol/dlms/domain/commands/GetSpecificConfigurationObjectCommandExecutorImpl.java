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
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificConfigurationObjectRequest;

@Component
public class GetSpecificConfigurationObjectCommandExecutorImpl extends
        BundleCommandExecutor<SpecificConfigurationObjectRequest, ActionResponseDto> implements
        GetSpecificConfigurationObjectCommandExecutor {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetSpecificConfigurationObjectCommandExecutorImpl.class);

    
    public GetSpecificConfigurationObjectCommandExecutorImpl() {
        super(SpecificConfigurationObjectRequest.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SpecificConfigurationObjectRequest requestData) throws ProtocolAdapterException {

        LOGGER.debug("Get specific configuration object for class id: {}, obis code: {}, attribute id: {}",
                requestData.getClassId(), this.getObisCode(requestData), requestData.getAttribute());

        final AttributeAddress attributeAddress = this.getAttributeAddress(requestData);

        try {
            List<GetResult> getResultList = conn.get(attributeAddress);
            if (this.checkResult(getResultList)) {
                final DataObject dataObject = getResultList.get(0).resultData();
                return new ActionResponseDto(this.dlmsHelper.getDebugInfo(dataObject));
            } else {
                return new ActionResponseDto(getResultList.get(0).resultCode().toString());
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

        return getResultList.get(0).resultData() != null;
    }

    private AttributeAddress getAttributeAddress(final SpecificConfigurationObjectRequest requestData) {
        return new AttributeAddress(requestData.getClassId(), this.getObisCode(requestData), requestData.getAttribute());
    }

    private ObisCode getObisCode(final SpecificConfigurationObjectRequest requestData) {
        final ObisCodeValuesDto obisDto = requestData.getObisCode();
        return new ObisCode((int) obisDto.getA(), (int) obisDto.getB(), (int) obisDto.getC(), (int) obisDto.getD(),
                (int) obisDto.getE(), (int) obisDto.getF());
    }
}
