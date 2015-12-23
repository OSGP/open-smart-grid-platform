/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategory;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQuery;

//0-0:96.11.0.255 Standard event Log
//0-0:96.11.1.255 Fraud detection Log
//0-0:96.11.3.255 M-Bus event log
//0-0:96-11.4.255 Communication Session event log
//
//Please the DLMS object in DLMS and DSMR documents

@Component()
public class RetrieveEventsCommandExecutor implements CommandExecutor<FindEventsQuery, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveEventsCommandExecutor.class);

    private static final int CLASS_ID = 7;
    private static final int ATTRIBUTE_ID = 2;

    // @formatter:off
    private static final EnumMap<EventLogCategory, ObisCode> eventLogCategoryObisCodeMap = new EnumMap<>(EventLogCategory.class);
    static{
        eventLogCategoryObisCodeMap.put(EventLogCategory.STANDARD_EVENT_LOG,        new ObisCode("0.0.96.11.0.255"));
        eventLogCategoryObisCodeMap.put(EventLogCategory.FRAUD_DETECTION_LOG,       new ObisCode("0.0.96.11.1.255"));
        eventLogCategoryObisCodeMap.put(EventLogCategory.COMMUNICATION_SESSION_LOG, new ObisCode("0.0.96.11.4.255"));
        eventLogCategoryObisCodeMap.put(EventLogCategory.M_BUS_EVENT_LOG,           new ObisCode("0.0.96.11.3.255"));
    }
    // @formatter:on

    @Override
    public AccessResultCode execute(final ClientConnection conn, final FindEventsQuery findEventsQuery)
            throws IOException, ProtocolAdapterException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID,
                eventLogCategoryObisCodeMap.get(findEventsQuery.getEventLogCategory()), ATTRIBUTE_ID);

        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving "
                    + findEventsQuery.getEventLogCategory());
        }

        // final GetResult result = getResultList.get(0);
        // final DataObject resultData = result.resultData();
        // if (resultData != null && resultData.isNumber()) {
        // return this.alarmHelperService.toAlarmTypes((Long)
        // result.resultData().value());
        // } else {
        // LOGGER.error("Result: {} --> {}", result.resultCode().value(),
        // result.resultData());
        // throw new
        // ProtocolAdapterException("Invalid register value received from the meter.");
        // }

        return AccessResultCode.SUCCESS;
    }
}
