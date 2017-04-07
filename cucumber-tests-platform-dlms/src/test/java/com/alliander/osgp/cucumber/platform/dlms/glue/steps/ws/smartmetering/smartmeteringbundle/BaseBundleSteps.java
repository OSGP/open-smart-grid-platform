/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import ma.glasnost.orika.MapperFacade;

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class BaseBundleSteps {

    @Autowired
    protected SmartMeteringBundleClient client;

    @Autowired
    protected MapperFacade defaultMapper;

    protected void ensureBundleResponse(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        if (ScenarioContext.Current().get(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSE) == null) {
            final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
            final BundleAsyncRequest asyncRequest = new BundleAsyncRequest();
            asyncRequest.setCorrelationUid(correlationUid);
            asyncRequest.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.DEVICE_IDENTIFICATION));

            final BundleResponse response = this.client.retrieveBundleResponse(asyncRequest);
            ScenarioContext.Current().put(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSE, response);

            assertEquals(ScenarioContext.Current().get(Keys.SCENARIO_CONTEXT_BUNDLE_ACTIONS),
                    response.getAllResponses().getResponseList().size());
        }
    }

    protected void increaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
        } else {
            ScenarioContext.Current().put(key, (Integer) ScenarioContext.Current().get(key) + 1);
        }
    }

    protected int getAndIncreaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
            return 0;
        }
        final Integer value = (Integer) ScenarioContext.Current().get(key);
        ScenarioContext.Current().put(key, value + 1);

        return value;
    }
}
