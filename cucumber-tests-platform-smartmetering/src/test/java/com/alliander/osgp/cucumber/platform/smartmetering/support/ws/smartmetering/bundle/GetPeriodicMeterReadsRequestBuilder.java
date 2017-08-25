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

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetPeriodicMeterReadsRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsRequestBuilder.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.DAILY;
    private static final DateTime DEFAULT_BEGIN_DATE = new DateTime(2016, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime DEFAULT_END_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);

    private PeriodType periodType;
    private XMLGregorianCalendar beginDate;
    private XMLGregorianCalendar endDate;

    public GetPeriodicMeterReadsRequestBuilder withDefaults() {
        this.periodType = DEFAULT_PERIOD_TYPE;
        this.beginDate = DateConverter.createXMLGregorianCalendar(DEFAULT_BEGIN_DATE.toDate());
        this.endDate = DateConverter.createXMLGregorianCalendar(DEFAULT_END_DATE.toDate());
        return this;
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
        if (parameters.containsKey(PlatformSmartmeteringKeys.KEY_PERIOD_TYPE)) {
            return PeriodType.fromValue(parameters.get(PlatformSmartmeteringKeys.KEY_PERIOD_TYPE));
        }
        LOGGER.debug("Key for period type not found in parameters, using default value.");
        return DEFAULT_PERIOD_TYPE;
    }

    private XMLGregorianCalendar getBeginDate(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.KEY_BEGIN_DATE)) {
            try {
                final Date beginDate = SDF.parse(parameters.get(PlatformSmartmeteringKeys.KEY_BEGIN_DATE));
                return DateConverter.createXMLGregorianCalendar(beginDate);
            } catch (final ParseException e) {
                LOGGER.debug("Error parsing begin date", e);
            }
        }
        LOGGER.debug("Key for begin date not found in parameters, using default value.");
        return DateConverter.createXMLGregorianCalendar(DEFAULT_BEGIN_DATE.toDate());
    }

    private XMLGregorianCalendar getEndDate(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.KEY_END_DATE)) {
            try {
                final Date endDate = SDF.parse(parameters.get(PlatformSmartmeteringKeys.KEY_END_DATE));
                return DateConverter.createXMLGregorianCalendar(endDate);
            } catch (final ParseException e) {
                LOGGER.debug("Error parsing end date", e);
            }
        }
        LOGGER.debug("Key for end date not found in parameters, using default value.");
        return DateConverter.createXMLGregorianCalendar(DEFAULT_END_DATE.toDate());
    }
}
