/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestMappingTest {

    private XMLGregorianCalendar xmlCalendar;
    private MapperFactory mapperFactory;

    @Before
    public void init() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        try {
            this.xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        // converter is needed because of instanceOf check to set boolean
        // mbusDevice
        this.mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullPeriodicReadsRequestData() {

        // build test data
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(null);

        // actual mapping
        this.mapperFactory.getMapperFacade().map(periodicReadsRequest, PeriodicMeterReadsQuery.class);

    }

    // Test to see if mapping succeeds when a PeriodicReadsRequest is completely
    // initialized.
    @Test
    public void testCompletePeriodicReadsRequestMapping() {

        // build test data
        final PeriodType periodType = PeriodType.DAILY;
        final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
        periodicReadsRequestData.setBeginDate(this.xmlCalendar);
        periodicReadsRequestData.setEndDate(this.xmlCalendar);
        periodicReadsRequestData.setPeriodType(periodType);
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(periodicReadsRequestData);

        // actual mapping
        final PeriodicMeterReadsQuery periodicMeterReadsQuery = this.mapperFactory.getMapperFacade().map(
                periodicReadsRequest, PeriodicMeterReadsQuery.class);

        // check mapping
        assertNotNull(periodicMeterReadsQuery);
        assertEquals(periodType.name(), periodicMeterReadsQuery.getPeriodType().name());
        final DateTime beginDateTime = new DateTime(periodicMeterReadsQuery.getBeginDate());
        assertEquals(this.xmlCalendar.getYear(), beginDateTime.getYear());
        assertEquals(this.xmlCalendar.getMonth(), beginDateTime.getMonthOfYear());
        assertEquals(this.xmlCalendar.getDay(), beginDateTime.getDayOfMonth());
        assertEquals(this.xmlCalendar.getHour(), beginDateTime.getHourOfDay());
        assertEquals(this.xmlCalendar.getMinute(), beginDateTime.getMinuteOfHour());
        assertEquals(this.xmlCalendar.getSecond(), beginDateTime.getSecondOfMinute());
        assertEquals(this.xmlCalendar.getMillisecond(), beginDateTime.getMillisOfSecond());
        final DateTime endDateTime = new DateTime(periodicMeterReadsQuery.getEndDate());
        assertEquals(this.xmlCalendar.getYear(), endDateTime.getYear());
        assertEquals(this.xmlCalendar.getMonth(), endDateTime.getMonthOfYear());
        assertEquals(this.xmlCalendar.getDay(), endDateTime.getDayOfMonth());
        assertEquals(this.xmlCalendar.getHour(), endDateTime.getHourOfDay());
        assertEquals(this.xmlCalendar.getMinute(), endDateTime.getMinuteOfHour());
        assertEquals(this.xmlCalendar.getSecond(), endDateTime.getSecondOfMinute());
        assertEquals(this.xmlCalendar.getMillisecond(), endDateTime.getMillisOfSecond());
        assertFalse(periodicMeterReadsQuery.isMbusDevice());

    }

}
