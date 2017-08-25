/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class FindEventsRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsRequestBuilder.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    private static final EventLogCategory DEFAULT_EVENT_LOG_CATEGORY = EventLogCategory.FRAUD_DETECTION_LOG;
    private static final DateTime DEFAULT_FROM = new DateTime(2016, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime DEFAULT_UNTIL = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);

    private EventLogCategory eventLogCategory;
    private XMLGregorianCalendar from;
    private XMLGregorianCalendar until;

    public FindEventsRequestBuilder withDefaults() {
        this.eventLogCategory = DEFAULT_EVENT_LOG_CATEGORY;
        this.from = DateConverter.createXMLGregorianCalendar(DEFAULT_FROM.toDate());
        this.until = DateConverter.createXMLGregorianCalendar(DEFAULT_UNTIL.toDate());
        return this;
    }

    public FindEventsRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.eventLogCategory = this.getEventLogCategory(parameters);
        this.from = this.getFrom(parameters);
        this.until = this.getUntil(parameters);
        return this;
    }

    public FindEventsRequest build() {
        final FindEventsRequest request = new FindEventsRequest();
        request.setEventLogCategory(this.eventLogCategory);
        request.setFrom(this.from);
        request.setUntil(this.until);
        return request;
    }

    private EventLogCategory getEventLogCategory(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.EVENT_TYPE)) {
            return EventLogCategory.fromValue(parameters.get(PlatformSmartmeteringKeys.EVENT_TYPE));
        }
        return DEFAULT_EVENT_LOG_CATEGORY;
    }

    private XMLGregorianCalendar getFrom(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.FROM_DATE)) {
            try {
                final Date from = SDF.parse(parameters.get(PlatformSmartmeteringKeys.FROM_DATE));
                return DateConverter.createXMLGregorianCalendar(from);
            } catch (final ParseException e) {
                LOGGER.debug("Error parsing from date", e);
            }
        }
        return DateConverter.createXMLGregorianCalendar(DEFAULT_FROM.toDate());
    }

    private XMLGregorianCalendar getUntil(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.UNTIL_DATE)) {
            try {
                final Date until = SDF.parse(parameters.get(PlatformSmartmeteringKeys.UNTIL_DATE));
                return DateConverter.createXMLGregorianCalendar(until);
            } catch (final ParseException e) {
                LOGGER.debug("Error parsing until date", e);
            }
        }
        return DateConverter.createXMLGregorianCalendar(DEFAULT_UNTIL.toDate());
    }
}
