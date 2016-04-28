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
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestMappingTest {

    private XMLGregorianCalendar xmlCalendar;
    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final PeriodType PERIODTYPE = PeriodType.DAILY;

    /** Needed to initialize a XMLGregorianCalendar object. */
    @Before
    public void init() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        try {
            this.xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if a NullPointerException is thrown when a PeriodicReadsRequest -
     * with a PeriodicReadsRequestData that is null - is mapped.
     */
    @Test(expected = NullPointerException.class)
    public void testWithNullPeriodicReadsRequestData() {

        // build test data
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(null);

        // actual mapping
        this.monitoringMapper.map(periodicReadsRequest, PeriodicMeterReadsQuery.class);

    }

    /**
     * Tests if a PeriodicReadsRequest object is mapped successfully when it is
     * completely initialized.
     */
    @Test
    public void testCompletePeriodicReadsRequestMapping() {

        // build test data
        final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
        periodicReadsRequestData.setBeginDate(this.xmlCalendar);
        periodicReadsRequestData.setEndDate(this.xmlCalendar);
        periodicReadsRequestData.setPeriodType(PERIODTYPE);
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(periodicReadsRequestData);

        // actual mapping
        final PeriodicMeterReadsQuery periodicMeterReadsQuery = this.monitoringMapper.map(periodicReadsRequest,
                PeriodicMeterReadsQuery.class);

        // check mapping
        assertNotNull(periodicMeterReadsQuery);
        assertNotNull(periodicMeterReadsQuery.getDeviceIdentification());
        assertNotNull(periodicMeterReadsQuery.getPeriodType());

        assertEquals(PERIODTYPE.name(), periodicMeterReadsQuery.getPeriodType().name());
        this.checkDateTimeMapping(periodicMeterReadsQuery.getBeginDate());
        this.checkDateTimeMapping(periodicMeterReadsQuery.getEndDate());
        assertFalse(periodicMeterReadsQuery.isMbusDevice());
        assertTrue(periodicMeterReadsQuery.getDeviceIdentification().isEmpty());

    }

    /**
     * Method checks the mapping of XMLGregorianCalendar objects to Date objects
     */
    private void checkDateTimeMapping(final Date date) {

        assertNotNull(date);

        // Cast to DateTime to enable comparison.
        final DateTime dateTime = new DateTime(date);

        assertEquals(this.xmlCalendar.getYear(), dateTime.getYear());
        assertEquals(this.xmlCalendar.getMonth(), dateTime.getMonthOfYear());
        assertEquals(this.xmlCalendar.getDay(), dateTime.getDayOfMonth());
        assertEquals(this.xmlCalendar.getHour(), dateTime.getHourOfDay());
        assertEquals(this.xmlCalendar.getMinute(), dateTime.getMinuteOfHour());
        assertEquals(this.xmlCalendar.getSecond(), dateTime.getSecondOfMinute());
        assertEquals(this.xmlCalendar.getMillisecond(), dateTime.getMillisOfSecond());
    }

}
