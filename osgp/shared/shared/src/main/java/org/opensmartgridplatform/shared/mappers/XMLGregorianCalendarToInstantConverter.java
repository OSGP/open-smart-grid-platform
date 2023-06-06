// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.mappers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLGregorianCalendarToInstantConverter
    extends BidirectionalConverter<XMLGregorianCalendar, Instant> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(XMLGregorianCalendarToInstantConverter.class);
  private static final int SECONDS_PER_MINUTE = 60;

  @Override
  public XMLGregorianCalendar convertFrom(
      final Instant source,
      final Type<XMLGregorianCalendar> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    try {
      final GregorianCalendar gregorianCalendar =
          GregorianCalendar.from(source.atZone(ZoneOffset.UTC));
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (final DatatypeConfigurationException e) {
      LOGGER.debug("newXMLGregorianCalendar failed", e);
      return null;
    }
  }

  @Override
  public Instant convertTo(
      final XMLGregorianCalendar source,
      final Type<Instant> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    if (source.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
      source.setTimezone(ZoneOffset.UTC.getTotalSeconds() / SECONDS_PER_MINUTE);
    }
    return source.toGregorianCalendar().toInstant();
  }

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    return (this.sourceType.isAssignableFrom(sourceType)
            && this.destinationType.isAssignableFrom(destinationType))
        || (this.destinationType.isAssignableFrom(sourceType)
            && this.sourceType.isAssignableFrom(destinationType));
  }
}
