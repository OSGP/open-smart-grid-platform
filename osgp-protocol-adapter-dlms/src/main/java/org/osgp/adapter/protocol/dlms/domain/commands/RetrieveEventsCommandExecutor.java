/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
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

    private static final int CLASS_ID_CLOCK = 8;
    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };
    private static final byte ATTRIBUTE_ID_TIME = 2;

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    @Autowired
    DataObjectToEventListConverter dataObjectToEventListConverter;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    // @formatter:off
    private static final EnumMap<EventLogCategory, ObisCode> EVENT_LOG_CATEGORY_OBISCODE_MAP = new EnumMap<>(EventLogCategory.class);
    static{
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.STANDARD_EVENT_LOG,        new ObisCode("0.0.99.98.0.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.FRAUD_DETECTION_LOG,       new ObisCode("0.0.99.98.1.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.COMMUNICATION_SESSION_LOG, new ObisCode("0.0.99.98.4.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.M_BUS_EVENT_LOG,           new ObisCode("0.0.99.98.3.255"));
    }
    // @formatter:on

    @Override
    public List<Event> execute(final LnClientConnection conn, final FindEventsQuery findEventsQuery)
            throws IOException, ProtocolAdapterException, TimeoutException {

        final List<Event> eventList = new ArrayList<>();

        final SelectiveAccessDescription selectiveAccessDescription = this.getSelectiveAccessDescription(
                findEventsQuery.getFrom(), findEventsQuery.getUntil());

        final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID,
                EVENT_LOG_CATEGORY_OBISCODE_MAP.get(findEventsQuery.getEventLogCategory()), ATTRIBUTE_ID,
                selectiveAccessDescription);

        final List<GetResult> getResultList = conn.get(configurationObjectValue);

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
                    + " from the meter resulted in" + result.resultCode());
        }

        final DataObject resultData = result.resultData();
        return this.dataObjectToEventListConverter.convert(resultData);
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final DateTime beginDateTime,
            final DateTime endDateTime) {

        final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_CLOCK), DataObject.newOctetStringData(OBIS_BYTES_CLOCK),
                DataObject.newInteger8Data(ATTRIBUTE_ID_TIME), DataObject.newUInteger16Data(0)));

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * As long as specifying a subset of captured objects from the buffer
         * through selectedValues does not work, retrieve all captured objects
         * by setting selectedValues to an empty array.
         */
        final DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject> emptyList());

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
    }
}
