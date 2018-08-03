/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.mappers;

import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class XMLGregorianCalendarToDateTimeConverter extends BidirectionalConverter<XMLGregorianCalendar, DateTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLGregorianCalendarToDateTimeConverter.class);

    @Override
    public XMLGregorianCalendar convertFrom(final DateTime source, final Type<XMLGregorianCalendar> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(source.toGregorianCalendar());
        } catch (final DatatypeConfigurationException e) {
            LOGGER.debug("newXMLGregorianCalendar failed", e);
            // Sonar will complain about not rethrowing the exception,
            // but we don't want that in this case!
            return null;
        }
    }

    @Override
    public DateTime convertTo(final XMLGregorianCalendar source, final Type<DateTime> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        return new DateTime(source.toGregorianCalendar().getTime());
    }

    @Override
    public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
        // The check 'this.sourceType.isAssignableFrom(sourceType)' fails for
        // org.yoda.DateTime.class.
        // Use custom check instead.
        if (Objects.equals(sourceType.getRawType().getName(), DateTime.class.getName())
                && Objects.equals(destinationType.getRawType().getName(), XMLGregorianCalendar.class.getName())) {
            return true;
        }
        return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
    }

}
