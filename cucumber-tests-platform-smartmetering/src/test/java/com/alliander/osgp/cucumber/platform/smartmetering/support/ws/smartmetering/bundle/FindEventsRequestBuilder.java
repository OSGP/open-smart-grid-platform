/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getDate;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;

import java.util.Collections;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class FindEventsRequestBuilder {

    private static final EventLogCategory DEFAULT_EVENT_LOG_CATEGORY = EventLogCategory.FRAUD_DETECTION_LOG;
    private static final DateTime DEFAULT_FROM = new DateTime(2016, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime DEFAULT_UNTIL = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);

    private EventLogCategory eventLogCategory;
    private XMLGregorianCalendar from;
    private XMLGregorianCalendar until;

    public FindEventsRequestBuilder withDefaults() {
        return this.fromParameterMap(Collections.emptyMap());
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
        return getEnum(parameters, PlatformSmartmeteringKeys.EVENT_TYPE, EventLogCategory.class,
                DEFAULT_EVENT_LOG_CATEGORY);
    }

    private XMLGregorianCalendar getFrom(final Map<String, String> parameters) {
        final DateTime dateTime = getDate(parameters, PlatformSmartmeteringKeys.FROM_DATE, DEFAULT_FROM);
        return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
    }

    private XMLGregorianCalendar getUntil(final Map<String, String> parameters) {
        final DateTime dateTime = getDate(parameters, PlatformSmartmeteringKeys.UNTIL_DATE, DEFAULT_UNTIL);
        return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
    }
}
