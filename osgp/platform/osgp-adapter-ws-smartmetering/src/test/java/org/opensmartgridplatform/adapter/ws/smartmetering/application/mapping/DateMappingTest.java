/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DateMappingTest {

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  /** Method creates an instance of XMLGregorianCalendar */
  private XMLGregorianCalendar createCalendar() {

    XMLGregorianCalendar xmlCalendar = null;
    try {
      xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    } catch (final DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return xmlCalendar;
  }

  @BeforeAll
  static void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  /** Tests the mapping of a Date object to a XMLGregorianCalendar object. */
  @Test
  void testDateToXMLGregorianCalendarMapping() {

    final Date date = new Date();
    final XMLGregorianCalendar xmlCalendar =
        this.mapperFactory.getMapperFacade().map(date, XMLGregorianCalendar.class);

    assertThat(xmlCalendar).isNotNull();

    // convert Date to a ZonedDateTime to enable comparison (Date has deprecated
    // method and test fails if these are used).
    final ZonedDateTime dateTime =
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

    assertThat(xmlCalendar.getYear()).isEqualTo(dateTime.getYear());
    assertThat(xmlCalendar.getMonth()).isEqualTo(dateTime.getMonth().getValue());
    assertThat(xmlCalendar.getDay()).isEqualTo(dateTime.getDayOfMonth());
    assertThat(xmlCalendar.getHour()).isEqualTo(dateTime.getHour());
    assertThat(xmlCalendar.getMinute()).isEqualTo(dateTime.getMinute());
    assertThat(xmlCalendar.getSecond()).isEqualTo(dateTime.getSecond());
    assertThat(xmlCalendar.getMillisecond()).isEqualTo(dateTime.getNano() / 1_000_000);
  }

  /** Test the mapping of an XMLGregorianCalendar to a Date object. */
  @Test
  void testXMLGregorianCalendarToDateMapping() {

    final XMLGregorianCalendar xmlCalendar = this.createCalendar();
    final Date date = this.mapperFactory.getMapperFacade().map(xmlCalendar, Date.class);

    assertThat(date).isNotNull();

    // convert Date to a ZonedDateTime to enable comparison (Date has deprecated
    // method and test fails if these are used).
    final ZonedDateTime dateTime =
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

    assertThat(dateTime.getYear()).isEqualTo(xmlCalendar.getYear());
    assertThat(dateTime.getMonth().getValue()).isEqualTo(xmlCalendar.getMonth());
    assertThat(dateTime.getDayOfMonth()).isEqualTo(xmlCalendar.getDay());
    assertThat(dateTime.getHour()).isEqualTo(xmlCalendar.getHour());
    assertThat(dateTime.getMinute()).isEqualTo(xmlCalendar.getMinute());
    assertThat(dateTime.getSecond()).isEqualTo(xmlCalendar.getSecond());
    assertThat(dateTime.getNano() / 1_000_000).isEqualTo(xmlCalendar.getMillisecond());
  }
}
