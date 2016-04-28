/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final Date DATE = new Date();
    private static final BigDecimal VALUE = new BigDecimal(1.0);
    private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
    private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;

    /** Tests if a MeterReads object can be mapped. */
    @Test
    public void testMeterReadsMapping() {

        // build test data
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
        final MeterReads meterReads = new MeterReads(DATE, osgpMeterValue, osgpMeterValue, osgpMeterValue,
                osgpMeterValue, osgpMeterValue, osgpMeterValue);

        // actual mapping
        final ActualMeterReadsResponse actualMeterReadsResponse = this.monitoringMapper.map(meterReads,
                ActualMeterReadsResponse.class);

        // check mapping
        assertNotNull(actualMeterReadsResponse);
        assertNotNull(actualMeterReadsResponse.getLogTime());
        this.checkDateMapping(actualMeterReadsResponse.getLogTime());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExport());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExportTariffOne());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExportTariffTwo());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImport());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImportTariffOne());
        this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImportTariffTwo());

    }

    /** Method checks mapping of OsgpMeterValue objects to MeterValue objects. */
    private void checkOsgpMeterValueMapping(final MeterValue meterValue) {

        assertNotNull(meterValue);
        assertEquals(OSGP_UNITTYPE, meterValue.getUnit());
        assertEquals(VALUE, meterValue.getValue());

    }

    /** Method check mapping of Date objects to XMLGregorianCalendar objects. */
    private void checkDateMapping(final XMLGregorianCalendar calendar) {

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(DATE);

        assertEquals(dateTime.getYear(), calendar.getYear());
        assertEquals(dateTime.getMonthOfYear(), calendar.getMonth());
        assertEquals(dateTime.getDayOfMonth(), calendar.getDay());
        assertEquals(dateTime.getHourOfDay(), calendar.getHour());
        assertEquals(dateTime.getMinuteOfHour(), calendar.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), calendar.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), calendar.getMillisecond());
    }
}
