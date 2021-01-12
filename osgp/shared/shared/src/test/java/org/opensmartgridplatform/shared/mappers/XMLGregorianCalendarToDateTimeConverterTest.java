/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

public class XMLGregorianCalendarToDateTimeConverterTest {

    /**
     * Simple converter implementation used to test mapping strategies.
     */
    private class DateTimeToStringConverter extends BidirectionalConverter<DateTime, String> {

        @Override
        public String convertTo(final DateTime source, final Type<String> destinationType,
                final MappingContext mappingContext) {
            if (source == null) {
                return null;
            }
            return source.toString();
        }

        @Override
        public DateTime convertFrom(final String source, final Type<DateTime> destinationType,
                final MappingContext mappingContext) {
            if (source == null) {
                return null;
            }
            return DateTime.parse(source);
        }

    }

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private MapperFacade mapper;

    /**
     * Register {@link XMLGregorianCalendarToDateTimeConverter} and
     * {@link DateTimeToStringConverter}. The former is the class under test.
     * The latter is just part of the unit tests.
     */
    @BeforeEach
    public void before() {
        this.mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        this.mapperFactory.getConverterFactory().registerConverter(new DateTimeToStringConverter());
        this.mapper = this.mapperFactory.getMapperFacade();
    }

    @Test
    public void mapXMLGregorianCalenderWithTimeZoneToDateTime() throws DatatypeConfigurationException {
        final String withTimeZone = "2010-06-30T01:20:30+02:00";
        final DateTime dateTime = DateTime.parse(withTimeZone);
        final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(withTimeZone);

        // Try to map to Joda version.
        final DateTime mappedJodaDateTime = this.mapper.map(xmlGregorianCalendar, DateTime.class);
        assertThat(mappedJodaDateTime).isEqualTo(dateTime);
    }

    @Test
    public void mapXMLGregorianWithoutTimeZoneCalenderToDateTime() throws DatatypeConfigurationException {
        final String withoutTimeZone = "2010-06-30T01:20:30";
        final DateTime dateTime = DateTime.parse(withoutTimeZone);
        final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(withoutTimeZone);

        // Try to map to Joda version.
        final DateTime mappedJodaDateTime = this.mapper.map(xmlGregorianCalendar, DateTime.class);
        assertThat(mappedJodaDateTime).isEqualTo(dateTime);
    }

    @Test
    public void mapDateTimeToXMLGregorianCalender() {
        try {
            final DateTime dateTime = DateTime.now();
            final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(dateTime.toGregorianCalendar());

            // Try to map to XML version.
            final XMLGregorianCalendar mappedXMLGregorianCalendar = this.mapper.map(dateTime,
                    XMLGregorianCalendar.class);
            assertThat(mappedXMLGregorianCalendar).isEqualTo(xmlGregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void mapStringToDateTime() {
        final String stringTime = DateTime.now().toString();

        // Try to map to Joda version.
        final DateTime mappedJodaTime = this.mapper.map(stringTime, DateTime.class);
        assertThat(mappedJodaTime).withFailMessage("Not expecting NULL but a DateTime instance.").isNotNull();
    }

    @Test
    public void mapDateTimeToString() {
        final DateTime dateTime = DateTime.now();

        // Try to map to String version.
        final String mappedStringTime = this.mapper.map(dateTime, String.class);
        assertThat(mappedStringTime).withFailMessage("Not expecting NULL but a date time as string.").isNotNull();
    }
}
