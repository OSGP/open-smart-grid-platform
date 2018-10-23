/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    /**
     * Tests the mapping of a Date object to a XMLGregorianCalendar object.
     */
    @Test
    public void testDateToXMLGregorianCalendarMapping() {

        final Date date = new Date();
        final XMLGregorianCalendar xmlCalendar = this.mapperFactory.getMapperFacade().map(date,
                XMLGregorianCalendar.class);

        assertNotNull(xmlCalendar);

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(date);

        assertEquals(dateTime.getYear(), xmlCalendar.getYear());
        assertEquals(dateTime.getMonthOfYear(), xmlCalendar.getMonth());
        assertEquals(dateTime.getDayOfMonth(), xmlCalendar.getDay());
        assertEquals(dateTime.getHourOfDay(), xmlCalendar.getHour());
        assertEquals(dateTime.getMinuteOfHour(), xmlCalendar.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), xmlCalendar.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), xmlCalendar.getMillisecond());
    }

    /**
     * Test the mapping of an XMLGregorianCalendar to a Date object.
     */
    @Test
    public void testXMLGregorianCalendarToDateMapping() {

        final XMLGregorianCalendar xmlCalendar = this.createCalendar();
        final Date date = this.mapperFactory.getMapperFacade().map(xmlCalendar, Date.class);

        assertNotNull(date);

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(date);
        assertEquals(xmlCalendar.getYear(), dateTime.getYear());
        assertEquals(xmlCalendar.getMonth(), dateTime.getMonthOfYear());
        assertEquals(xmlCalendar.getDay(), dateTime.getDayOfMonth());
        assertEquals(xmlCalendar.getHour(), dateTime.getHourOfDay());
        assertEquals(xmlCalendar.getMinute(), dateTime.getMinuteOfHour());
        assertEquals(xmlCalendar.getSecond(), dateTime.getSecondOfMinute());
        assertEquals(xmlCalendar.getMillisecond(), dateTime.getMillisOfSecond());
    }

    /**
     * Method creates an instance of XMLGregorianCalendar
     */
    private XMLGregorianCalendar createCalendar() {

        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return xmlCalendar;
    }

}
