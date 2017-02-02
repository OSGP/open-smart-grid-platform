/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.builders;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;

public class ProfileGenericDataRequestBuilder {

    private PeriodType periodType = PeriodType.DAILY;
    private Date beginDate = new Date();
    private Date endDate = new DateTime(this.beginDate).plusDays(1).toDate();

    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    public ProfileGenericDataRequestBuilder() {
        this.marshaller.setContextPath(GetPeriodicMeterReadsRequest.class.getPackage().getName());
    }

    public ProfileGenericDataRequestBuilder withPeriodType(final PeriodType periodType) {
        this.periodType = periodType;
        return this;
    }

    public ProfileGenericDataRequestBuilder withBeginDate(final Date beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public ProfileGenericDataRequestBuilder withEndDate(final Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public ProfileGenericDataRequest build() {
        final ProfileGenericDataRequest result = new ProfileGenericDataRequest();
        // result.setPeriodType(this.periodType);
        // result.setBeginDate(this.createXMLGregorianCalendar(this.beginDate));
        // result.setEndDate(this.createXMLGregorianCalendar(this.endDate));

        return result;
    }

    // TODO naar utuls
    public XMLGregorianCalendar createXMLGregorianCalendar(final Date date) {
        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
