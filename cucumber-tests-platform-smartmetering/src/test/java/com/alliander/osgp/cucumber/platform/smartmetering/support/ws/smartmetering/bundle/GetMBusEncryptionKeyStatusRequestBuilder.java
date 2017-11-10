/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.Collections;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMBusEncryptionKeyStatusRequest;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetMBusEncryptionKeyStatusRequestBuilder {

    private static final String DEFAULT_MBUS_DEVICE_IDENTIFICATION = "TESTG102400000001";

    private String mbusDeviceIdentification;

    public GetMBusEncryptionKeyStatusRequestBuilder withDefaults() {
        return this.fromParameterMap(Collections.emptyMap());
    }

    public GetMBusEncryptionKeyStatusRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.mbusDeviceIdentification = this.getMBusDeviceIdentification(parameters);
        return this;
    }

    public GetMBusEncryptionKeyStatusRequest build() {
        final GetMBusEncryptionKeyStatusRequest request = new GetMBusEncryptionKeyStatusRequest();
        request.setMBusDeviceIdentification(this.mbusDeviceIdentification);
        return request;
    }

    private String getMBusDeviceIdentification(final Map<String, String> parameters) {
        return Helpers.getString(parameters, PlatformSmartmeteringKeys.MBUS_DEVICE_IDENTIFICATION,
                DEFAULT_MBUS_DEVICE_IDENTIFICATION);
    }
}
