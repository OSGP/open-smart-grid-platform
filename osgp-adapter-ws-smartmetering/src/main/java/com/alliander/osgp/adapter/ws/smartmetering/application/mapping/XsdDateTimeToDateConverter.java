package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * convert a xsd datetime string to a date and back
 * 
 * @author dev
 *
 */
public class XsdDateTimeToDateConverter extends BidirectionalConverter<String, Date> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XsdDateTimeToDateConverter.class);

    @Override
    public Date convertTo(final String source, final Type<Date> destinationType) {
        if (source == null) {
            return null;
        }
        try {
            return new Date(DatatypeFactory.newInstance().newXMLGregorianCalendar(source).getMillisecond());
        } catch (final DatatypeConfigurationException e) {
            LOGGER.warn("wrong datetime " + source, e);
            return null;
        }
    }

    @Override
    public String convertFrom(final Date source, final Type<String> destinationType) {
        if (source == null) {
            return null;
        }
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(source);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal).toXMLFormat();
        } catch (final DatatypeConfigurationException e) {
            LOGGER.warn("datetime conversion problem" + source, e);
            return null;
        }
    }

}
