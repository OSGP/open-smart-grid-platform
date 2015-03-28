package com.alliander.osgp.acceptancetests;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

public class DateUtils {

    public static XMLGregorianCalendar convertToXMLGregorianCalendar(final String dateString, final String format)
            throws DatatypeConfigurationException, ParseException {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        final DateTime dateTime = new DateTime(sdf.parse(dateString));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
    }
}
