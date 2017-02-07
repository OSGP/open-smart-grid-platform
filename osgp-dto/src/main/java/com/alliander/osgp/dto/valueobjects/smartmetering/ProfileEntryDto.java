/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.datatype.XMLGregorianCalendar;

public class ProfileEntryDto implements Serializable {

    private static final long serialVersionUID = 2123390296585369209L;

    private final String stringValue;
    private final XMLGregorianCalendar dateValue;
    private final BigDecimal floatValue;
    private final Long longValue;

    public ProfileEntryDto(String stringValue) {
        this(stringValue, null, null, null);
    }

    public ProfileEntryDto(XMLGregorianCalendar dateValue) {
        this(null, dateValue, null, null);
    }

    public ProfileEntryDto(BigDecimal floatValue) {
        this(null, null, floatValue, null);
    }

    public ProfileEntryDto(Long longValue) {
        this(null, null, null, longValue);
    }

    public ProfileEntryDto(String stringValue, XMLGregorianCalendar dateValue, BigDecimal floatValue, Long longValue) {
        super();
        this.stringValue = stringValue;
        this.dateValue = dateValue;
        this.floatValue = floatValue;
        this.longValue = longValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public XMLGregorianCalendar getDateValue() {
        return this.dateValue;
    }

    public BigDecimal getFloatValue() {
        return this.floatValue;
    }

    public Long getLongValue() {
        return this.longValue;
    }

}
