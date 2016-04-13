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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsGasMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // classmap is needed because of different variable names
    @Before
    public void init() {
        this.mapperFactory.classMap(OsgpMeterValue.class, MeterValue.class).field("osgpUnit", "unit").byDefault()
        .register();
    }

    // Test to check if a MeterReadsGas object can be mapped
    @Test
    public void testMeterReadsGasMapping() {

        // build test data
        final Date date = new Date();
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(new BigDecimal(1.0), OsgpUnit.KWH);
        final MeterReadsGas meterReadsGas = new MeterReadsGas(date, osgpMeterValue, date);

        // actual mapping
        final ActualMeterReadsGasResponse actualMeterReadsGasResponse = this.mapperFactory.getMapperFacade().map(
                meterReadsGas, ActualMeterReadsGasResponse.class);

        // check mapping
        assertNotNull(actualMeterReadsGasResponse);

        this.checkDateMapping(date, actualMeterReadsGasResponse.getCaptureTime());
        this.checkDateMapping(date, actualMeterReadsGasResponse.getLogTime());

        assertEquals(osgpMeterValue.getOsgpUnit().name(), actualMeterReadsGasResponse.getConsumption().getUnit().name());
        assertEquals(osgpMeterValue.getValue(), actualMeterReadsGasResponse.getConsumption().getValue());
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
