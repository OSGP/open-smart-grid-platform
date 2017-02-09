package com.alliander.osgp.platform.cucumber.helpers;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateHelper {

    private DateHelper() {
    }

    public static XMLGregorianCalendar createXMLGregorianCalendar(final Date date) {
        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
