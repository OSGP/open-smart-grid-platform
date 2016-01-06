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
import org.openmuc.jdlms.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;

@Component(value = "dataObjectToEventListConverter")
public class DataObjectToEventListConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectToEventListConverter.class);

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public List<Event> convert(final DataObject source) throws ProtocolAdapterException {
        final List<Event> eventList = new ArrayList<>();
        if (source == null) {
            throw new ProtocolAdapterException("DataObject should not be null");
        }

        final List<DataObject> listOfEvents = source.value();
        for (final DataObject eventDataObject : listOfEvents) {
            eventList.add(this.getEvent(eventDataObject));
        }

        return eventList;

    }

    private Event getEvent(final DataObject eventDataObject) throws ProtocolAdapterException {

        final List<DataObject> eventData = eventDataObject.value();

        if (eventData == null) {
            throw new ProtocolAdapterException("eventData DataObject should not be null");
        }

        if (eventData.size() != 2) {
            throw new ProtocolAdapterException("eventData size should be 2");
        }

        final DateTime dateTime = this.dlmsHelperService.convertDataObjectToDateTime(eventData.get(0));
        final Short code = eventData.get(1).value();

        LOGGER.info("Event time is {} and event code is {}", dateTime, code);

        final Event event = new Event(dateTime, code.intValue());
        return event;
    }
}
