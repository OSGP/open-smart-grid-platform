/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.mappers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class XMLGregorianCalendarToZonedDateTimeConverter
        extends BidirectionalConverter<XMLGregorianCalendar, ZonedDateTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLGregorianCalendarToZonedDateTimeConverter.class);

    @Override
    public XMLGregorianCalendar convertFrom(final ZonedDateTime source,
            final Type<XMLGregorianCalendar> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        try {
            final GregorianCalendar gregorianCalendar = GregorianCalendar.from(source);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (
            final DatatypeConfigurationException e
        ) {
            LOGGER.debug("newXMLGregorianCalendar failed", e);
            // Sonar will complain about not rethrowing the exception,
            // but we don't want that in this case!
            return null;
        }
    }

    @Override
    public ZonedDateTime convertTo(final XMLGregorianCalendar source, final Type<ZonedDateTime> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        final ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("ETC"));
        return source.toGregorianCalendar().toZonedDateTime().toLocalDateTime().atZone(zoneId);
    }

    @Override
    public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
        // The check 'this.sourceType.isAssignableFrom(sourceType)' fails for
        // org.joda.DateTime.class.
        // Use custom check instead.
        return Objects.equals(sourceType.getRawType().getName(), ZonedDateTime.class.getName())
                && Objects.equals(destinationType.getRawType().getName(), XMLGregorianCalendar.class.getName())
                || this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
    }

}
