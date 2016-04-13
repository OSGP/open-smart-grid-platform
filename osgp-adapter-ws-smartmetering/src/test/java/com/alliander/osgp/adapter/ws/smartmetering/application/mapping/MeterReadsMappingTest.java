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

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // This converter is needed because of the M_3 in the OsgpUnitType enum
    // and the M3 in the OsgpUnit enum.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new MeterValueConverter());
    }

    // Test to check if a MeterReads object can be mapped.
    @Test
    public void testMeterReadsMapping() {

        // build test data
        final Date date = new Date();
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(new BigDecimal(1.0), OsgpUnit.M3);
        final MeterReads meterReads = new MeterReads(date, osgpMeterValue, osgpMeterValue, osgpMeterValue,
                osgpMeterValue, osgpMeterValue, osgpMeterValue);

        // actual mapping
        final ActualMeterReadsResponse actualMeterReadsResponse = this.mapperFactory.getMapperFacade().map(meterReads,
                ActualMeterReadsResponse.class);

        // check mapping
        assertNotNull(actualMeterReadsResponse);
        this.checkDateMapping(date, actualMeterReadsResponse.getLogTime());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyExport());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyExportTariffOne());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyExportTariffTwo());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyImport());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyImportTariffOne());
        this.checkOsgpMeterValueMapping(osgpMeterValue, actualMeterReadsResponse.getActiveEnergyImportTariffTwo());

    }

    // method to check the mapping of OsgpMeterValue objects
    private void checkOsgpMeterValueMapping(final OsgpMeterValue osgpMeterValue, final MeterValue meterValue) {

        assertEquals(OsgpUnitType.M_3, meterValue.getUnit());
        assertEquals(osgpMeterValue.getValue(), meterValue.getValue());

    }

    // method to check the mapping of Date objects
    private void checkDateMapping(final Date date, final XMLGregorianCalendar calendar) {

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(date);

        assertEquals(dateTime.getYear(), calendar.getYear());
        assertEquals(dateTime.getMonthOfYear(), calendar.getMonth());
        assertEquals(dateTime.getDayOfMonth(), calendar.getDay());
        assertEquals(dateTime.getHourOfDay(), calendar.getHour());
        assertEquals(dateTime.getMinuteOfHour(), calendar.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), calendar.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), calendar.getMillisecond());
    }
}
