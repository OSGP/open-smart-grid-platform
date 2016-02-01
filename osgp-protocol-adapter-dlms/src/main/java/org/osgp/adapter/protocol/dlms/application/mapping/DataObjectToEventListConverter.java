/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategory;

@Component(value = "dataObjectToEventListConverter")
public class DataObjectToEventListConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectToEventListConverter.class);

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public List<Event> convert(final DataObject source, final EventLogCategory eventLogCategory)
            throws ProtocolAdapterException {
        final List<Event> eventList = new ArrayList<>();
        if (source == null) {
            throw new ProtocolAdapterException("DataObject should not be null");
        }

        final List<DataObject> listOfEvents = source.value();
        for (final DataObject eventDataObject : listOfEvents) {
            eventList.add(this.getEvent(eventDataObject, eventLogCategory));
        }

        return eventList;

    }

    private Event getEvent(final DataObject eventDataObject, final EventLogCategory eventLogCategory)
            throws ProtocolAdapterException {

        final List<DataObject> eventData = eventDataObject.value();

        if (eventData == null) {
            throw new ProtocolAdapterException("eventData DataObject should not be null");
        }

        if (eventData.size() != eventLogCategory.getNumberOfEventElements()) {
            throw new ProtocolAdapterException("eventData size should be "
                    + eventLogCategory.getNumberOfEventElements());
        }

        final DateTime dateTime = this.dlmsHelperService.convertDataObjectToDateTime(eventData.get(0));
        if (!eventData.get(1).isNumber()) {
            throw new ProtocolAdapterException("eventData value is not a number");
        }
        final Short code = eventData.get(1).value();

        Integer eventCounter = null;

        if (eventLogCategory.getNumberOfEventElements() == 3) {
            if (!eventData.get(2).isNumber()) {
                throw new ProtocolAdapterException("eventData value is not a number");
            }
            eventCounter = eventData.get(2).value();
        }

        LOGGER.info("Event time is {}, event code is {} and event counter is {}", dateTime, code, eventCounter);

        return new Event(dateTime, code.intValue(), eventCounter);
    }
}
