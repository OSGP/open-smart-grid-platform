/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;

import java.util.Map;

import org.joda.time.DateTime;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ProfileGenericDataRequestFactory {

    private ProfileGenericDataRequestFactory() {
        // Private constructor for utility class
    }

    public static ProfileGenericDataRequest fromParameterMap(final Map<String, String> requestParameters) {
        final ProfileGenericDataRequest profileGenericDataRequest = new ProfileGenericDataRequest();
        final DateTime beginDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_BEGIN_DATE);
        final DateTime endDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_END_DATE);
        final ObisCodeValues obisCodeValues = ObisCodeValuesFactory.fromParameterMap(requestParameters);
        final CaptureObjectDefinitions captureObjecDefinitions = CaptureObjectDefinitionsFactory
                .fromParameterMap(requestParameters);

        profileGenericDataRequest
                .setDeviceIdentification(requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        profileGenericDataRequest.setBeginDate(DateConverter.createXMLGregorianCalendar(beginDate.toDate()));
        profileGenericDataRequest.setEndDate(DateConverter.createXMLGregorianCalendar(endDate.toDate()));
        profileGenericDataRequest.setObisCode(obisCodeValues);
        profileGenericDataRequest.setSelectedValues(captureObjecDefinitions);

        return profileGenericDataRequest;
    }

    private static DateTime dateFromParameterMap(final Map<String, String> requestParameters, final String key) {
        return getDate(requestParameters, key, new DateTime());
    }

    public static ProfileGenericDataAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final ProfileGenericDataAsyncRequest profileGenericDataAsyncRequest = new ProfileGenericDataAsyncRequest();
        profileGenericDataAsyncRequest.setCorrelationUid(correlationUid);
        profileGenericDataAsyncRequest.setDeviceIdentification(deviceIdentification);
        return profileGenericDataAsyncRequest;
    }
}
