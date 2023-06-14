// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.mappers;

import java.time.ZoneId;
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

public class XMLGregorianCalendarToZonedDateTimeConverter
    extends BidirectionalConverter<XMLGregorianCalendar, ZonedDateTime> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(XMLGregorianCalendarToZonedDateTimeConverter.class);

  @Override
  public XMLGregorianCalendar convertFrom(
      final ZonedDateTime source,
      final Type<XMLGregorianCalendar> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    try {
      final GregorianCalendar gregorianCalendar = GregorianCalendar.from(source);
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (final DatatypeConfigurationException e) {
      LOGGER.debug("newXMLGregorianCalendar failed", e);
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

    if (source.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
      // Optional timezone field is empty for source
      final ZoneId zoneId = ZoneId.of("UTC");
      return source.toGregorianCalendar().toZonedDateTime().toLocalDateTime().atZone(zoneId);
    } else {
      return source.toGregorianCalendar().toZonedDateTime();
    }
  }

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    return Objects.equals(sourceType.getRawType().getName(), ZonedDateTime.class.getName())
            && Objects.equals(
                destinationType.getRawType().getName(), XMLGregorianCalendar.class.getName())
        || this.sourceType.isAssignableFrom(sourceType)
            && this.destinationType.isAssignableFrom(destinationType);
  }
}
