// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.mappers;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Objects;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLGregorianCalendarToDateTimeConverter
    extends BidirectionalConverter<XMLGregorianCalendar, ZonedDateTime> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(XMLGregorianCalendarToDateTimeConverter.class);

  @Override
  public XMLGregorianCalendar convertFrom(
      final ZonedDateTime source,
      final Type<XMLGregorianCalendar> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(source));
    } catch (final DatatypeConfigurationException e) {
      LOGGER.debug("newXMLGregorianCalendar failed", e);
      // Sonar will complain about not rethrowing the exception,
      // but we don't want that in this case!
      return null;
    }
  }

  @Override
  public ZonedDateTime convertTo(
      final XMLGregorianCalendar source,
      final Type<ZonedDateTime> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final ZoneId timeZone = this.timeZoneOf(source);
    if (timeZone == null) {
      return ZonedDateTime.ofInstant(
          source.toGregorianCalendar().getTime().toInstant(), ZoneId.systemDefault());
    }
    return ZonedDateTime.ofInstant(source.toGregorianCalendar().getTime().toInstant(), timeZone);
  }

  private ZoneOffset timeZoneOf(final XMLGregorianCalendar source) {
    final int offsetMinutes = source.getTimezone();
    if (offsetMinutes == DatatypeConstants.FIELD_UNDEFINED) {
      return null;
    }
    return ZoneOffset.ofHoursMinutes(offsetMinutes / 60, offsetMinutes % 60);
  }

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    // The check 'this.sourceType.isAssignableFrom(sourceType)' fails for
    // GregorianCalendar.
    // Use custom check instead.
    return Objects.equals(sourceType.getRawType().getName(), ZonedDateTime.class.getName())
            && Objects.equals(
                destinationType.getRawType().getName(), XMLGregorianCalendar.class.getName())
        || this.sourceType.isAssignableFrom(sourceType)
            && this.destinationType.equals(destinationType);
  }
}
