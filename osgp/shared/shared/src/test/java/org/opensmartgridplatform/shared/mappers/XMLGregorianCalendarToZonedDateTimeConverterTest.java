/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XMLGregorianCalendarToZonedDateTimeConverterTest {

  public static final ZoneId UTC = ZoneId.of("UTC");

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
  private MapperFacade mapper;

  /**
   * Register {@link
   * org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToZonedDateTimeConverter} and
   * {@link ZonedDateTimeToStringConverter}. The former is the class under test. The latter is just
   * part of the unit tests.
   */
  @BeforeEach
  public void before() {
    this.mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToZonedDateTimeConverter());
    this.mapper = this.mapperFactory.getMapperFacade();
  }

  @Test
  public void mapXMLGregorianCalenderWithTimeZoneToZonedDateTime()
      throws DatatypeConfigurationException {
    final String withTimeZone = "2010-06-30T01:20:30+02:00";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withTimeZone);

    final ZonedDateTime expectedDateTime = ZonedDateTime.parse(withTimeZone);

    final ZonedDateTime mappedZonedDateTime =
        this.mapper.map(xmlGregorianCalendar, ZonedDateTime.class);
    assertThat(mappedZonedDateTime).isEqualTo(expectedDateTime);
  }

  @Test
  public void mapXMLGregorianCalenderCloseToDayEndWithTimeZoneToZonedDateTime()
      throws DatatypeConfigurationException {
    final String withTimeZone = "2020-03-30T23:50:30+03:00";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withTimeZone);

    final ZonedDateTime expectedDateTime = ZonedDateTime.parse(withTimeZone);

    final ZonedDateTime mappedZonedDateTime =
        this.mapper.map(xmlGregorianCalendar, ZonedDateTime.class);
    assertThat(mappedZonedDateTime).isEqualTo(expectedDateTime);
  }

  @Test
  public void mapXMLGregorianWithoutTimeZoneCalenderToZonedDateTime()
      throws DatatypeConfigurationException {
    final String withoutTimeZone = "2010-06-30T01:20:30";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withoutTimeZone);

    final LocalDateTime localDate = LocalDateTime.parse(withoutTimeZone);
    final ZonedDateTime expectedDateTime = localDate.atZone(UTC);

    final ZonedDateTime mappedZonedDateTime =
        this.mapper.map(xmlGregorianCalendar, ZonedDateTime.class);
    assertThat(mappedZonedDateTime).isEqualTo(expectedDateTime);
  }

  @Test
  public void mapZonedDateTimeToXMLGregorianCalender() throws DatatypeConfigurationException {
    final ZonedDateTime dateTime = ZonedDateTime.now();

    final XMLGregorianCalendar expectedXmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTime));

    final XMLGregorianCalendar mappedXmlGregorianCalendar =
        this.mapper.map(dateTime, XMLGregorianCalendar.class);
    assertThat(mappedXmlGregorianCalendar).isEqualTo(expectedXmlGregorianCalendar);
  }
}
