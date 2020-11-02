/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "dataObjectToEventListConverter")
public class DataObjectToEventListConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectToEventListConverter.class);

    private final DlmsHelper dlmsHelper;

    @Autowired
    public DataObjectToEventListConverter(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;
    }

    public List<EventDto> convert(final DataObject source, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {
        final List<EventDto> eventList = new ArrayList<>();
        if (source == null) {
            throw new ProtocolAdapterException("DataObject should not be null");
        }

        final List<DataObject> listOfEvents = source.getValue();
        for (final DataObject eventDataObject : listOfEvents) {
            eventList.add(this.getEvent(eventDataObject, eventLogCategory));
        }

        return eventList;

    }

    private EventDto getEvent(final DataObject eventDataObject, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {

        final List<DataObject> eventData = eventDataObject.getValue();

        if (eventData == null) {
            throw new ProtocolAdapterException("eventData DataObject should not be null");
        }

        if (eventData.size() != eventLogCategory.getNumberOfEventElements()) {
            throw new ProtocolAdapterException(
                    "eventData size should be " + eventLogCategory.getNumberOfEventElements());
        }

        if (eventLogCategory == EventLogCategoryDto.POWER_FAILURE_EVENT_LOG) {
            return getPowerFailureEvent(eventData, eventLogCategory);
        } else {
            return getEvent(eventData, eventLogCategory);
        }
    }

    private EventDto getEvent(List<DataObject> eventData, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {
        // extract values from List<DataObject> eventData.
        final DateTime dateTime = this.extractDateTime(eventData);
        final Short code = this.extractCode(eventData);
        final Integer eventCounter = this.extractEventCounter(eventLogCategory, eventData);
        final String eventLogCategoryName = eventLogCategory.name();

        LOGGER.info("Event time is {}, event code is {}, event category is {} and event counter is {}", dateTime, code,
                eventLogCategoryName, eventCounter);

        // build a new EventDto with those values.
        return new EventDto(dateTime, code.intValue(), eventCounter, eventLogCategoryName);
    }

    private EventDto getPowerFailureEvent(List<DataObject> eventData, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {
        final DateTime endTime = this.extractDateTime(eventData);
        final Short code = 1;
        final Long duration = this.extractEventDuration(eventLogCategory, eventData);
        final String eventLogCategoryName = eventLogCategory.name();
        final DateTime startTime = calculatePowerFailureStartTime(endTime, duration);

        LOGGER.info("Event time is {}, event code is {}, event category is {} and event duration is {}", endTime, code,
                eventLogCategoryName, duration);

        return new EventDto(endTime, code.intValue(), eventLogCategoryName, startTime, duration);
    }

    private DateTime extractDateTime(final List<DataObject> eventData) throws ProtocolAdapterException {

        final DateTime dateTime = this.dlmsHelper.convertDataObjectToDateTime(eventData.get(0)).asDateTime();
        if (dateTime == null) {
            throw new ProtocolAdapterException("eventData time is null/unspecified");
        }
        return dateTime;
    }

    private Long extractEventDuration(EventLogCategoryDto eventLogCategory, List<DataObject> eventData)
            throws ProtocolAdapterException {
        if (!eventData.get(1).isNumber()) {
            throw new ProtocolAdapterException("eventData value is not a number");
        }
        return eventData.get(1).getValue();
    }

    private DateTime calculatePowerFailureStartTime(DateTime endTime, Long duration) {
        return endTime.minusSeconds(duration.intValue());
    }

    private Short extractCode(final List<DataObject> eventData) throws ProtocolAdapterException {
        if (!eventData.get(1).isNumber()) {
            throw new ProtocolAdapterException("eventData value is not a number");
        }
        return eventData.get(1).getValue();
    }

    private Integer extractEventCounter(final EventLogCategoryDto eventLogCategory, final List<DataObject> eventData)
            throws ProtocolAdapterException {

        Integer eventCounter = null;

        if (eventLogCategory.getNumberOfEventElements() == 3) {
            if (!eventData.get(2).isNumber()) {
                throw new ProtocolAdapterException("eventData value is not a number");
            }
            eventCounter = eventData.get(2).getValue();
        }

        return eventCounter;
    }
}
