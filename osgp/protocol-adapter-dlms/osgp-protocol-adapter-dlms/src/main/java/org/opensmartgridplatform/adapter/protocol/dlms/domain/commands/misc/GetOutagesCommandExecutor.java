/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetOutagesCommandExecutor extends AbstractCommandExecutor<GetOutagesRequestDto, List<OutageDto>> {

    private static final int CLASS_ID = 7;
    private static final int ATTRIBUTE_ID = 2;
    private static final String OBIS_CODE = "1.0.99.97.0.255";
    private final DataObjectToOutageListConverter dataObjectToOutageListConverter;

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
                "RetrieveOutages, retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(eventLogBuffer));

        final GetResult getResult;
        try {
            getResult = conn.getConnection().get(eventLogBuffer);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving event register POWER_FAILURE_EVENT_LOG");
        }

        if (!AccessResultCode.SUCCESS.equals(getResult.getResultCode())) {
            log.info("Result of getting events for POWER_FAILURE_EVENT_LOG is {}", getResult.getResultCode());
            throw new ProtocolAdapterException(
                    "Getting the outages from POWER_FAILURE_EVENT_LOG from the meter resulted in: "
                            + getResult.getResultCode());
        }

        final DataObject resultData = getResult.getResultData();
        return this.dataObjectToOutageListConverter.convert(resultData);
    }

}
