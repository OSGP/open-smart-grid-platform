/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusEncryptionKeyStatusRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusEncryptionKeyStatusResponseDto;

@Component()
public class GetMBusEncryptionKeyStatusCommandExecutor
        extends AbstractCommandExecutor<GetMBusEncryptionKeyStatusRequestDto, GetMBusEncryptionKeyStatusResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMBusEncryptionKeyStatusCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final Map<Short, ObisCode> OBIS_CODES = new HashMap<>();
    private static final int ATTRIBUTE_ID = 14;

    public GetMBusEncryptionKeyStatusCommandExecutor() {
        super(GetMBusEncryptionKeyStatusRequestDto.class);
    }

    @PostConstruct
    private static void initObisCodes() {
        OBIS_CODES.put((short) 1, new ObisCode("0.1.24.1.0.255"));
        OBIS_CODES.put((short) 2, new ObisCode("0.2.24.1.0.255"));
        OBIS_CODES.put((short) 3, new ObisCode("0.3.24.1.0.255"));
        OBIS_CODES.put((short) 4, new ObisCode("0.4.24.1.0.255"));
    }

    // @Override
    // public GetMBusEncryptionKeyStatusRequestDto fromBundleRequestInput(final
    // ActionRequestDto bundleInput)
    // throws ProtocolAdapterException {
    //
    // this.checkActionRequestType(bundleInput);
    //
    // return (GetMBusEncryptionKeyStatusRequestDto) bundleInput;
    // }
    //
    // @Override
    // public ActionResponseDto asBundleResponse(final
    // GetMBusEncryptionKeyStatusResponseDto executionResult)
    // throws ProtocolAdapterException {
    //
    // return executionResult;
    // }

    @Override
    public GetMBusEncryptionKeyStatusResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final GetMBusEncryptionKeyStatusRequestDto request) throws ProtocolAdapterException {

        final ObisCode obisCode = OBIS_CODES.get(request.getChannel());

        final AttributeAddress getParameter = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription("GetMBusEncryptionKeyStatus, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(getParameter));

        LOGGER.info(
                "Retrieving current M-Bus encryption key status by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                CLASS_ID, obisCode, ATTRIBUTE_ID);

        GetResult getResult = null;
        try {
            getResult = conn.getConnection().get(getParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException("No GetResult received while retrieving M-Bus encryption key status.");
        }

        final DataObject dataObject = getResult.getResultData();
        if (!dataObject.isNumber()) {
            throw new ProtocolAdapterException("Received unexpected result data.");
        }

        final EncryptionKeyStatusTypeDto encryptionKeyStatusType = EncryptionKeyStatusTypeDto
                .fromValue((Integer) dataObject.getValue());
        return new GetMBusEncryptionKeyStatusResponseDto(request.getMBusDeviceIdentification(),
                encryptionKeyStatusType);
    }
}
