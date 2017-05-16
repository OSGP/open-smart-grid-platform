/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.builders;

import org.joda.time.DateTime;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;

public class ProfileGenericDataRequestBuilder {

    private String deviceIdentification;
    private ObisCodeValues obisCode;
    private DateTime beginDate = new DateTime();
    private DateTime endDate = new DateTime();
    private CaptureObjectDefinitions captureObjectDefinitions;

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

    public ProfileGenericDataRequestBuilder withSelectedValues(
            final CaptureObjectDefinitions captureObjectDefinitions) {
        this.captureObjectDefinitions = captureObjectDefinitions;
        return this;
    }

    public ProfileGenericDataRequest build() {
        final ProfileGenericDataRequest result = new ProfileGenericDataRequest();
        result.setDeviceIdentification(this.deviceIdentification);
        result.setObisCode(this.obisCode);
        result.setBeginDate(DateConverter.createXMLGregorianCalendar(this.beginDate.toDate()));
        result.setEndDate(DateConverter.createXMLGregorianCalendar(this.endDate.toDate()));
        result.setSelectedValues(this.captureObjectDefinitions);
        return result;
    }

}
