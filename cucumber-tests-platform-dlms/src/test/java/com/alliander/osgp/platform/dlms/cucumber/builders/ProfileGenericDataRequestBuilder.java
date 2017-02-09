/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.builders;

import org.joda.time.DateTime;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.platform.cucumber.helpers.DateHelper;

public class ProfileGenericDataRequestBuilder {

    private String deviceIdentification;
    private ObisCodeValues obisCode;
    private DateTime beginDate = new DateTime();
    private DateTime endDate = new DateTime();

    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    public ProfileGenericDataRequestBuilder() {
        this.marshaller.setContextPath(ProfileGenericDataRequest.class.getPackage().getName());
    }

    public ProfileGenericDataRequestBuilder withDeviceidentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public ProfileGenericDataRequestBuilder withObisCode(final ObisCodeValues obisCode) {
        this.obisCode = obisCode;
        return this;
    }

    public ProfileGenericDataRequestBuilder withBeginDate(final DateTime beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public ProfileGenericDataRequestBuilder withEndDate(final DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public ProfileGenericDataRequest build() {
        final ProfileGenericDataRequest result = new ProfileGenericDataRequest();
        result.setDeviceIdentification(this.deviceIdentification);
        result.setObisCode(this.obisCode);
        result.setBeginDate(DateHelper.createXMLGregorianCalendar(this.beginDate.toDate()));
        result.setEndDate(DateHelper.createXMLGregorianCalendar(this.endDate.toDate()));
        return result;
    }

}
