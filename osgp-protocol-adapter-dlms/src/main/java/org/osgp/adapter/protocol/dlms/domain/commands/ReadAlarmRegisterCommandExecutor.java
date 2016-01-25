/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;

@Component
public class ReadAlarmRegisterCommandExecutor implements CommandExecutor<ReadAlarmRegisterRequest, AlarmRegister> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAlarmRegisterCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private AlarmHelperService alarmHelperService;

    @Override
    public AlarmRegister execute(final LnClientConnection conn, final DlmsDevice device,
            final ReadAlarmRegisterRequest object) throws IOException, TimeoutException, ProtocolAdapterException {

        return new AlarmRegister(this.retrieveAlarmRegister(conn));
    }

    private Set<AlarmType> retrieveAlarmRegister(final LnClientConnection conn) throws IOException, TimeoutException,
            ProtocolAdapterException {

        final AttributeAddress alarmRegisterValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final List<GetResult> getResultList = conn.get(alarmRegisterValue);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving alarm register.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving alarm register, got "
                    + getResultList.size());
        }

        final GetResult result = getResultList.get(0);
        final DataObject resultData = result.resultData();
        if (resultData != null && resultData.isNumber()) {
            return this.alarmHelperService.toAlarmTypes((Long) result.resultData().value());
        } else {
            LOGGER.error("Result: {} --> {}", result.resultCode().name(), result.resultData());
            throw new ProtocolAdapterException("Invalid register value received from the meter.");
        }
    }
}
