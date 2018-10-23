/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class PeriodicReadsRequestDataFactory {

    public static PeriodicReadsRequestData fromParameterMap(final Map<String, String> requestParameters) {

        final PeriodType periodType = PeriodType
                .fromValue(getString(requestParameters, PlatformSmartmeteringKeys.KEY_PERIOD_TYPE, "DAILY"));
        final XMLGregorianCalendar beginDate = createXMLGregorianCalendar(requestParameters,
                PlatformKeys.KEY_BEGIN_DATE);
        final XMLGregorianCalendar endDate = createXMLGregorianCalendar(requestParameters, PlatformKeys.KEY_END_DATE);

        final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
        periodicReadsRequestData.setBeginDate(beginDate);
        periodicReadsRequestData.setEndDate(endDate);
        periodicReadsRequestData.setPeriodType(periodType);

        return periodicReadsRequestData;
    }

    private static final XMLGregorianCalendar createXMLGregorianCalendar(final Map<String, String> settings,
            final String key) {

        final DateTime dateTime = getDate(settings, key, new DateTime());
        return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
    }

}
