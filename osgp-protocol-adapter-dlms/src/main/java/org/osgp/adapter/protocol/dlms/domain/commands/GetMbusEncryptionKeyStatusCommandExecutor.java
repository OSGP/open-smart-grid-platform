/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.HashMap;
import java.util.Map;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.MbusClientAttribute;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.domain.valueobjects.EncryptionKeyStatusType;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;

@Component()
public class GetMbusEncryptionKeyStatusCommandExecutor
        extends AbstractCommandExecutor<GetMbusEncryptionKeyStatusRequestDto, GetMbusEncryptionKeyStatusResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMbusEncryptionKeyStatusCommandExecutor.class);

    private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
    private static final Map<Short, ObisCode> OBIS_CODES = new HashMap<>();
    private static final int ATTRIBUTE_ID = MbusClientAttribute.ENCRYPTION_KEY_STATUS.attributeId();

    static {
        OBIS_CODES.put((short) 1, new ObisCode("0.1.24.1.0.255"));
        OBIS_CODES.put((short) 2, new ObisCode("0.2.24.1.0.255"));
        OBIS_CODES.put((short) 3, new ObisCode("0.3.24.1.0.255"));
        OBIS_CODES.put((short) 4, new ObisCode("0.4.24.1.0.255"));
    }

    public GetMbusEncryptionKeyStatusCommandExecutor() {
        super(GetMbusEncryptionKeyStatusRequestDto.class);
    }

    @Override
    public GetMbusEncryptionKeyStatusResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final GetMbusEncryptionKeyStatusRequestDto request) throws ProtocolAdapterException {

        final EncryptionKeyStatusTypeDto encryptionKeyStatusType = this
                .getEncryptionKeyStatusTypeDto(request.getChannel(), conn);
        return new GetMbusEncryptionKeyStatusResponseDto(request.getMbusDeviceIdentification(),
                encryptionKeyStatusType);
    }

    public EncryptionKeyStatusTypeDto getEncryptionKeyStatusTypeDto(final short channel,
            final DlmsConnectionHolder conn) throws ProtocolAdapterException {

        final ObisCode obisCode = OBIS_CODES.get(channel);

        final AttributeAddress getParameter = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription("GetMbusEncryptionKeyStatusByChannel, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(getParameter));

        LOGGER.info(
                "Retrieving current M-Bus encryption key status by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                CLASS_ID, obisCode, ATTRIBUTE_ID);

        final DataObject dataObject = new ConnectionAndResultHelper().getValidatedResultData(conn, getParameter);

        return EncryptionKeyStatusTypeDto
                .valueOf(EncryptionKeyStatusType.fromValue((Integer) dataObject.getValue()).name());
    }

}
