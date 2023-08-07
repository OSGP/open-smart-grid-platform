// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XMLGregorianCalendarToDateTimeConverterTest {

  /** Simple converter implementation used to test mapping strategies. */
  private class DateTimeToStringConverter extends BidirectionalConverter<ZonedDateTime, String> {

    @Override
    public String convertTo(
        final ZonedDateTime source,
        final Type<String> destinationType,
        final MappingContext mappingContext) {
      if (source == null) {
        return null;
      }
      return source.toString();
    }

    @Override
    public ZonedDateTime convertFrom(
        final String source,
        final Type<ZonedDateTime> destinationType,
        final MappingContext mappingContext) {
      if (source == null) {
        return null;
      }
      return ZonedDateTime.parse(source);
    }
  }

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
  private MapperFacade mapper;

  /**
   * Register {@link
   * org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter} and {@link
   * DateTimeToStringConverter}. The former is the class under test. The latter is just part of the
   * unit tests.
   */
  @BeforeEach
  public void before() {
    this.mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    this.mapperFactory.getConverterFactory().registerConverter(new DateTimeToStringConverter());
    this.mapper = this.mapperFactory.getMapperFacade();
  }

  @Test
  public void mapXMLGregorianCalenderWithTimeZoneToDateTime()
      throws DatatypeConfigurationException {
    final String withTimeZone = "2010-06-30T01:20:30+02:00";
    final ZonedDateTime dateTime = ZonedDateTime.parse(withTimeZone);
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withTimeZone);

    // Try to map to Joda version.
    final ZonedDateTime mappedJodaDateTime =
        this.mapper.map(xmlGregorianCalendar, ZonedDateTime.class);
    assertThat(mappedJodaDateTime).isEqualTo(dateTime);
  }

  @Test
  public void mapXMLGregorianWithoutTimeZoneCalenderToDateTime()
      throws DatatypeConfigurationException {
    final String withoutTimeZone = "2010-06-30T01:20:30";
    final LocalDateTime dateTimeLocal = LocalDateTime.parse(withoutTimeZone);
    final ZonedDateTime dateTimeWithDefaultZone = dateTimeLocal.atZone(ZoneId.systemDefault());
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(withoutTimeZone);

    // Try to map to Joda version.
    final ZonedDateTime mappedJodaDateTime =
        this.mapper.map(xmlGregorianCalendar, ZonedDateTime.class);
    assertThat(mappedJodaDateTime).isEqualTo(dateTimeWithDefaultZone);
  }

  @Test
  public void mapDateTimeToXMLGregorianCalender() {
    try {
      final ZonedDateTime dateTime = ZonedDateTime.now();
      final XMLGregorianCalendar xmlGregorianCalendar =
          DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTime));

      // Try to map to XML version.
      final XMLGregorianCalendar mappedXMLGregorianCalendar =
          this.mapper.map(dateTime, XMLGregorianCalendar.class);
      assertThat(mappedXMLGregorianCalendar).isEqualTo(xmlGregorianCalendar);
    } catch (final DatatypeConfigurationException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void mapStringToDateTime() {
    final String stringTime = ZonedDateTime.now().toString();

    // Try to map to Joda version.
    final ZonedDateTime mappedJodaTime = this.mapper.map(stringTime, ZonedDateTime.class);
    assertThat(mappedJodaTime)
        .withFailMessage("Not expecting NULL but a DateTime instance.")
        .isNotNull();
  }

  @Test
  public void mapDateTimeToString() {
    final ZonedDateTime dateTime = ZonedDateTime.now();

    // Try to map to String version.
    final String mappedStringTime = this.mapper.map(dateTime, String.class);
    assertThat(mappedStringTime)
        .withFailMessage("Not expecting NULL but a date time as string.")
        .isNotNull();
  }
}
