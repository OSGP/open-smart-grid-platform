/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.builders;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;

public class PeriodicMeterReadsRequestBuilder {

    private String deviceIdentification;
    private PeriodType periodType;
    private XMLGregorianCalendar beginDate;
    private XMLGregorianCalendar endDate;

    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    public PeriodicMeterReadsRequestBuilder() {
        this.marshaller.setContextPath(PeriodicMeterReadsRequest.class.getPackage().getName());
    }

    public PeriodicMeterReadsRequestBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public PeriodicMeterReadsRequestBuilder withPeriodType(final PeriodType periodType) {
        this.periodType = periodType;
        return this;
    }

    public PeriodicMeterReadsRequestBuilder withBeginDate(final DateTime beginDate) {
        this.beginDate = this.createXMLGregorianCalendar(beginDate);
        return this;
    }

    public PeriodicMeterReadsRequestBuilder withEndDate(final DateTime endDate) {
        this.endDate = this.createXMLGregorianCalendar(endDate);
        return this;
    }

    public PeriodicMeterReadsRequest build() {
        final PeriodicMeterReadsRequest request = new PeriodicMeterReadsRequest();
        final PeriodicReadsRequestData requestData = new PeriodicReadsRequestData();

        requestData.setPeriodType(this.periodType);
        requestData.setBeginDate(this.beginDate);
        requestData.setEndDate(this.endDate);
        request.setDeviceIdentification(this.deviceIdentification);
        request.setPeriodicReadsRequestData(requestData);

        return request;
    }

    XMLGregorianCalendar createXMLGregorianCalendar(final DateTime date) {
        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date.toDate());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
