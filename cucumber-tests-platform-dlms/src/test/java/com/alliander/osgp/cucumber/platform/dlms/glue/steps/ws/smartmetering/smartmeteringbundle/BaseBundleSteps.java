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

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Action;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
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

    protected void ensureBundleResponse() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        if (ScenarioContext.Current().get(Keys.BUNDLE_RESPONSE) == null) {
            final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
            final BundleAsyncRequest asyncRequest = new BundleAsyncRequest();
            asyncRequest.setCorrelationUid(correlationUid);
            asyncRequest.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.DEVICE_IDENTIFICATION));

            final BundleResponse response = this.client.retrieveBundleResponse(asyncRequest);
            ScenarioContext.Current().put(Keys.BUNDLE_RESPONSE, response);

            assertEquals(ScenarioContext.Current().get(Keys.BUNDLE_ACTION_COUNT),
                    response.getAllResponses().getResponseList().size());
        }
    }

    protected void addActionToBundleRequest(final BundleRequest bundleRequest, final Action action) {
        bundleRequest.getActions().getActionList().add(action);
        this.increaseCount(Keys.BUNDLE_ACTION_COUNT);
    }

    protected Response getNextBundleResponse()
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        this.ensureBundleResponse();
        final BundleResponse bundleResponse = (BundleResponse) ScenarioContext.Current().get(Keys.BUNDLE_RESPONSE);
        return bundleResponse.getAllResponses().getResponseList()
                .get(this.getAndIncreaseCount(Keys.BUNDLE_RESPONSE_COUNT));
    }

    private void increaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
        } else {
            ScenarioContext.Current().put(key, (Integer) ScenarioContext.Current().get(key) + 1);
        }
    }

    private int getAndIncreaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
            return 0;
        }
        final Integer value = (Integer) ScenarioContext.Current().get(key);
        ScenarioContext.Current().put(key, value + 1);

        return value;
    }
}
