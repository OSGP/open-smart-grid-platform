/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.installation;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class AddDeviceRequestFactory {

    private AddDeviceRequestFactory() {
        // Private constructor for utility class.
    }

    public static AddDeviceRequest fromParameterMap(final Map<String, String> requestParameters) {
        final AddDeviceRequest addDeviceRequest = new AddDeviceRequest();
        final Device device = DeviceFactory.fromParameterMap(requestParameters);
        addDeviceRequest.setDevice(device);
        return addDeviceRequest;
    }

    public static AddDeviceAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper
                .getDeviceIdentificationFromStepData(requestParameters);
        final AddDeviceAsyncRequest addDeviceAsyncRequest = new AddDeviceAsyncRequest();
        addDeviceAsyncRequest.setCorrelationUid(correlationUid);
        addDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
        return addDeviceAsyncRequest;
    }
}
