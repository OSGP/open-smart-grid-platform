/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.Collections;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetPeriodicMeterReadsRequestBuilder {

    private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.DAILY;
    private static final DateTime DEFAULT_BEGIN_DATE = new DateTime(2016, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime DEFAULT_END_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);

    private PeriodType periodType;
    private XMLGregorianCalendar beginDate;
    private XMLGregorianCalendar endDate;

    public GetPeriodicMeterReadsRequestBuilder withDefaults() {
        return this.fromParameterMap(Collections.emptyMap());
    }

    public GetPeriodicMeterReadsRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.periodType = this.getPeriodType(parameters);
        this.beginDate = this.getBeginDate(parameters);
        this.endDate = this.getEndDate(parameters);
        return this;
    }

    public GetPeriodicMeterReadsRequest build() {
        final GetPeriodicMeterReadsRequest request = new GetPeriodicMeterReadsRequest();
        request.setPeriodType(this.periodType);
        request.setBeginDate(this.beginDate);
        request.setEndDate(this.endDate);
        return request;
    }

    private PeriodType getPeriodType(final Map<String, String> parameters) {
        return Helpers.getEnum(parameters, PlatformSmartmeteringKeys.KEY_PERIOD_TYPE, PeriodType.class,
                DEFAULT_PERIOD_TYPE);
    }

    private XMLGregorianCalendar getBeginDate(final Map<String, String> parameters) {
        final DateTime dateTime = Helpers.getDate(parameters, PlatformSmartmeteringKeys.KEY_BEGIN_DATE,
                DEFAULT_BEGIN_DATE);
        return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
    }

    private XMLGregorianCalendar getEndDate(final Map<String, String> parameters) {
        final DateTime dateTime = Helpers.getDate(parameters, PlatformSmartmeteringKeys.KEY_END_DATE, DEFAULT_END_DATE);
        return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
    }
}
