/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static com.alliander.osgp.cucumber.core.Helpers.getDate;

import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class PeriodicReadsRequestDataFactory {

    public static PeriodicReadsRequestData fromParameterMap(final Map<String, String> requestParameters) {

        final PeriodType periodType = PeriodType
                .fromValue(Helpers.getString(requestParameters, PlatformSmartmeteringKeys.KEY_PERIOD_TYPE, "DAILY"));
        final XMLGregorianCalendar beginDate = createXMLGregorianCalendar(requestParameters,
                PlatformKeys.KEY_BEGIN_DATE);
        final XMLGregorianCalendar endDate = createXMLGregorianCalendar(requestParameters, PlatformKeys.KEY_END_DATE);

        final PeriodicReadsRequestData request = new PeriodicReadsRequestData();
        request.setBeginDate(beginDate);
        request.setEndDate(endDate);
        request.setPeriodType(periodType);

        return request;
    }

    private static final XMLGregorianCalendar createXMLGregorianCalendar(final Map<String, String> settings,
            final String key) {

        final DateTime date = getDate(settings, key, new DateTime());

        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date.toDate());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
