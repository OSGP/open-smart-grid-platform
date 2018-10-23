/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class FindMessageLogsRequestFactory {
    private FindMessageLogsRequestFactory() {
        // Private constructor for utility class
    }

    public static FindMessageLogsRequest fromParameterMap(final Map<String, String> requestParameters) {
        final FindMessageLogsRequest findMessageLogsRequest = new FindMessageLogsRequest();
        findMessageLogsRequest.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return findMessageLogsRequest;
    }

    public static FindMessageLogsAsyncRequest fromScenarioContext() {
        final FindMessageLogsAsyncRequest findMessageLogsAsyncRequest = new FindMessageLogsAsyncRequest();
        findMessageLogsAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        findMessageLogsAsyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return findMessageLogsAsyncRequest;
    }
}
