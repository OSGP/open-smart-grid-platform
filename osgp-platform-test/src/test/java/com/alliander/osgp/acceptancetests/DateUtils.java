/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
