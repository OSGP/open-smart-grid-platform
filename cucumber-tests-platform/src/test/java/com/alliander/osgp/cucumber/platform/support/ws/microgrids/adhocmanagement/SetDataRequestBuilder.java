/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement;

import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier;
import com.alliander.osgp.adapter.ws.schema.microgrids.common.AsyncRequest;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;

public class SetDataRequestBuilder {

    private SetDataRequestBuilder() {
        // Private constructor for utility class.
    }

    public static SetDataRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetDataRequest setDataRequest = new SetDataRequest();
        setDataRequest.setDeviceIdentification(requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));

        final List<SetDataSystemIdentifier> systems = new SetDataSystemIdentifierBuilder()
                .withSettings(requestParameters).buildList();
        setDataRequest.getSystem().addAll(systems);

        return setDataRequest;
    }

    public static SetDataAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            throw new AssertionError("ScenarioContext must contain the correlation UID for key \""
                    + Keys.KEY_CORRELATION_UID + "\" before creating an async request.");
        }
        final String deviceIdentification = requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION);
        if (deviceIdentification == null) {
            throw new AssertionError("The Step DataTable must contain the device identification for key \""
                    + Keys.KEY_DEVICE_IDENTIFICATION + "\" when creating an async request.");
        }
        final SetDataAsyncRequest setDataAsyncRequest = new SetDataAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceId(deviceIdentification);
        setDataAsyncRequest.setAsyncRequest(asyncRequest);
        return setDataAsyncRequest;
    }
}
