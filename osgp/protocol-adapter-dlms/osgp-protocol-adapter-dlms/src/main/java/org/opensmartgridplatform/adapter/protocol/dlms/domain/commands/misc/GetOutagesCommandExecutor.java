/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToOutageListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetOutagesCommandExecutor extends AbstractCommandExecutor<GetOutagesRequestDto, List<OutageDto>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOutagesCommandExecutor.class);

    private static final int CLASS_ID = 7;
    private static final int ATTRIBUTE_ID = 2;

    private final DataObjectToOutageListConverter dataObjectToOutageListConverter;

    private static final String OBIS_CODE = "1.0.99.97.0.255";

    @Autowired
    public GetOutagesCommandExecutor(final DataObjectToOutageListConverter dataObjectToOutageListConverter) {
        super(GetOutagesRequestDto.class);
        this.dataObjectToOutageListConverter = dataObjectToOutageListConverter;
    }

    @Override
    public ActionResponseDto asBundleResponse(final List<OutageDto> executionResult) throws ProtocolAdapterException {
        return new GetOutagesResponseDto(executionResult);
    }

    @Override
    public List<OutageDto> execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetOutagesRequestDto getOutagesRequestDto) throws ProtocolAdapterException {

        final AttributeAddress eventLogBuffer = new AttributeAddress(CLASS_ID, new ObisCode(OBIS_CODE), ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription(
                "RetrieveEvents for " + getOutagesRequestDto.getEventLogCategory() + 
                ", retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(eventLogBuffer));

        final GetResult getResult;
        try {
            getResult = conn.getConnection().get(eventLogBuffer);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving event register " + getOutagesRequestDto.getEventLogCategory());
        }

        if (!AccessResultCode.SUCCESS.equals(getResult.getResultCode())) {
            LOGGER.info("Result of getting events for {} is {}", getOutagesRequestDto.getEventLogCategory(),
                    getResult.getResultCode());
            throw new ProtocolAdapterException(
                    "Getting the events for  " + getOutagesRequestDto.getEventLogCategory() + " from the meter resulted in: "
                            + getResult.getResultCode());
        }

        final DataObject resultData = getResult.getResultData();
        return this.dataObjectToOutageListConverter.convert(resultData, getOutagesRequestDto);
    }

}
