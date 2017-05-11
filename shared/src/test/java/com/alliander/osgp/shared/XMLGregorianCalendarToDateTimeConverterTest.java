/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

public class XMLGregorianCalendarToDateTimeConverterTest {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private MapperFacade mapper;

    @Before
    public void before() {
        this.mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        this.mapper = this.mapperFactory.getMapperFacade();
    }

    @Test
    public void test1() {
        try {
            final DateTime dateTime = DateTime.now();
            final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    dateTime.toGregorianCalendar());

            // Try to map to Yoda version.
            final DateTime mappedYodaDateTime = this.mapper.map(xmlGregorianCalendar, DateTime.class);
            Assert.assertEquals(dateTime, mappedYodaDateTime);
        } catch (final DatatypeConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test2() {
        try {
            final DateTime dateTime = DateTime.now();
            final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    dateTime.toGregorianCalendar());

            // Try to map to XML version.
            final XMLGregorianCalendar mappedXMLGregorianCalendar = this.mapper.map(dateTime,
                    XMLGregorianCalendar.class);
            Assert.assertEquals(xmlGregorianCalendar, mappedXMLGregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
}
