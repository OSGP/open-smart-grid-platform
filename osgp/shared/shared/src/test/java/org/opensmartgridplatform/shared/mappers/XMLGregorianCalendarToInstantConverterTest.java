//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

class XMLGregorianCalendarToInstantConverterTest {

  public static final ZoneId UTC = ZoneId.of("UTC");

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
  private MapperFacade mapper;

  @BeforeEach
  void before() {
    this.mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToInstantConverter());
    this.mapper = this.mapperFactory.getMapperFacade();
  }

  @Test
  void mapsToInstantFromXmlGregorianCalenderWithTimeZone() throws DatatypeConfigurationException {
    final String withTimeZone = "2010-06-30T01:20:30+02:00";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withTimeZone);

    final Instant expected = ZonedDateTime.parse(withTimeZone).toInstant();

    final Instant actual = this.mapper.map(xmlGregorianCalendar, Instant.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void mapsToInstantFromXmlGregorianCalenderCloseToDayEndWithTimeZone()
      throws DatatypeConfigurationException {
    final String withTimeZone = "2020-03-30T23:50:30+03:00";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withTimeZone);

    final Instant expected = ZonedDateTime.parse(withTimeZone).toInstant();

    final Instant actual = this.mapper.map(xmlGregorianCalendar, Instant.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void mapsToInstantFromXmlGregorianCalenderWithoutTimeZone()
      throws DatatypeConfigurationException {
    final String withoutTimeZone = "2010-06-30T01:20:30";
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withoutTimeZone);

    final LocalDateTime localDate = LocalDateTime.parse(withoutTimeZone);
    final Instant expected = localDate.toInstant(ZoneOffset.UTC);

    final Instant actual = this.mapper.map(xmlGregorianCalendar, Instant.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void mapsToXmlGregorianCalenderFromInstant() throws DatatypeConfigurationException {
    final Instant instant = Instant.now();

    final XMLGregorianCalendar expected =
        DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(GregorianCalendar.from(instant.atZone(UTC)));

    final XMLGregorianCalendar actual = this.mapper.map(instant, XMLGregorianCalendar.class);
    assertThat(actual).isEqualTo(expected);
  }
}
