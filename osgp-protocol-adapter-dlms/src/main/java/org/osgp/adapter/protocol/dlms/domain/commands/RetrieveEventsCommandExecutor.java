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
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategory;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQuery;

@Component()
public class RetrieveEventsCommandExecutor implements CommandExecutor<FindEventsQuery, List<Event>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveEventsCommandExecutor.class);

    private static final int CLASS_ID = 7;
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    DataObjectToEventListConverter dataObjectToEventListConverter;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    // @formatter:off
    private static final EnumMap<EventLogCategory, ObisCode> EVENT_LOG_CATEGORY_OBISCODE_MAP = new EnumMap<>(
            EventLogCategory.class);
    static {
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.STANDARD_EVENT_LOG,        new ObisCode("0.0.99.98.0.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.FRAUD_DETECTION_LOG,       new ObisCode("0.0.99.98.1.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.COMMUNICATION_SESSION_LOG, new ObisCode("0.0.99.98.4.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.M_BUS_EVENT_LOG,           new ObisCode("0.0.99.98.3.255"));
    }
    // @formatter:on

    @Override
    public List<Event> execute(final LnClientConnection conn, final DlmsDevice device,
            final FindEventsQuery findEventsQuery) throws ProtocolAdapterException {

        final AttributeAddress eventLogBuffer = new AttributeAddress(CLASS_ID,
                EVENT_LOG_CATEGORY_OBISCODE_MAP.get(findEventsQuery.getEventLogCategory()), ATTRIBUTE_ID);

        List<GetResult> getResultList;
        try {
            getResultList = conn.get(eventLogBuffer);
        } catch (IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving event register "
                    + findEventsQuery.getEventLogCategory());
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving event log for "
                    + findEventsQuery.getEventLogCategory() + ". Got " + getResultList.size());
        }

        final GetResult result = getResultList.get(0);
        if (!AccessResultCode.SUCCESS.equals(result.resultCode())) {
            LOGGER.info("Result of getting events for {} is {}", findEventsQuery.getEventLogCategory(),
                    result.resultCode());
            throw new ProtocolAdapterException("Getting the events for  " + findEventsQuery.getEventLogCategory()
                    + " from the meter resulted in: " + result.resultCode());
        }

        final DataObject resultData = result.resultData();
        return this.dataObjectToEventListConverter.convert(resultData, findEventsQuery.getEventLogCategory());
    }

}
