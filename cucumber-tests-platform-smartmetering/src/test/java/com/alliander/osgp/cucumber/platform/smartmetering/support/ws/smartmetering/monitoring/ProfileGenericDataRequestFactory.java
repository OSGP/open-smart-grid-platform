/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static com.alliander.osgp.cucumber.core.Helpers.getDate;

import java.util.Map;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ProfileGenericDataRequestFactory {

    private ProfileGenericDataRequestFactory() {
        // Private constructor for utility class
    }

    public static ProfileGenericDataRequest fromParameterMap(final Map<String, String> requestParameters) {
        final ProfileGenericDataRequest request = new ProfileGenericDataRequest();
        final DateTime beginDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_BEGIN_DATE);
        final DateTime endDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_END_DATE);
        final ObisCodeValues obisCodeValues = ObisCodeValuesFactory.fromParameterMap(requestParameters);
        final CaptureObjectDefinitions captureObjecDefinitions = CaptureObjectDefinitionsFactory
                .fromParameterMap(requestParameters);

        request.setDeviceIdentification(requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        request.setBeginDate(DateConverter.createXMLGregorianCalendar(beginDate.toDate()));
        request.setEndDate(DateConverter.createXMLGregorianCalendar(endDate.toDate()));
        request.setObisCode(obisCodeValues);
        request.setSelectedValues(captureObjecDefinitions);

        return request;
    }

    private static DateTime dateFromParameterMap(final Map<String, String> requestParameters, final String key) {
        return getDate(requestParameters, key, new DateTime());
    }

    public static ProfileGenericDataAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final ProfileGenericDataAsyncRequest asyncRequest = new ProfileGenericDataAsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceIdentification(deviceIdentification);
        return asyncRequest;
    }
}
