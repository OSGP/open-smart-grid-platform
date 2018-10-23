/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetAdministrativeStatusRequestFactory {
    private SetAdministrativeStatusRequestFactory() {
        // Private constructor for utility class
    }

    public static SetAdministrativeStatusRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetAdministrativeStatusRequest setAdministrativeStatusRequest = new SetAdministrativeStatusRequest();
        setAdministrativeStatusRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        setAdministrativeStatusRequest.setEnabled(AdministrativeStatusType
                .valueOf(requestParameters.get(PlatformSmartmeteringKeys.ADMINISTRATIVE_STATUS_TYPE)));

        return setAdministrativeStatusRequest;
    }

    public static SetAdministrativeStatusAsyncRequest fromScenarioContext() {
        final SetAdministrativeStatusAsyncRequest setAdministrativeStatusAsyncRequest = new SetAdministrativeStatusAsyncRequest();
        setAdministrativeStatusAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        setAdministrativeStatusAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return setAdministrativeStatusAsyncRequest;
    }
}
