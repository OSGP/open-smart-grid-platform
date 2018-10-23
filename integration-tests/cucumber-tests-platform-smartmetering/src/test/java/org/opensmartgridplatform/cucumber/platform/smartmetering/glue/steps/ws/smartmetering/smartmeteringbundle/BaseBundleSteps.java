/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Action;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

import ma.glasnost.orika.MapperFacade;

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class BaseBundleSteps {

    @Autowired
    protected SmartMeteringBundleClient client;

    @Autowired
    protected MapperFacade mapperFacade;

    protected void ensureBundleResponse() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        if (ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE) == null) {
            final String correlationUid = (String) ScenarioContext.current()
                    .get(PlatformSmartmeteringKeys.KEY_CORRELATION_UID);
            final BundleAsyncRequest asyncRequest = new BundleAsyncRequest();
            asyncRequest.setCorrelationUid(correlationUid);
            asyncRequest.setDeviceIdentification(
                    (String) ScenarioContext.current().get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

            final BundleResponse response = this.client.retrieveBundleResponse(asyncRequest);
            ScenarioContext.current().put(PlatformSmartmeteringKeys.BUNDLE_RESPONSE, response);

            assertEquals(ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_ACTION_COUNT),
                    response.getAllResponses().getResponseList().size());
        }
    }

    protected void addActionToBundleRequest(final Action action) {

        final BundleRequest bundleRequest = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        bundleRequest.getActions().getActionList().add(action);
        this.increaseCount(PlatformSmartmeteringKeys.BUNDLE_ACTION_COUNT);
    }

    protected Response getNextBundleResponse()
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        this.ensureBundleResponse();
        final BundleResponse bundleResponse = (BundleResponse) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);
        return bundleResponse.getAllResponses().getResponseList()
                .get(this.getAndIncreaseCount(PlatformSmartmeteringKeys.BUNDLE_RESPONSE_COUNT));
    }

    private void increaseCount(final String key) {
        if (ScenarioContext.current().get(key) == null) {
            ScenarioContext.current().put(key, 1);
        } else {
            ScenarioContext.current().put(key, (Integer) ScenarioContext.current().get(key) + 1);
        }
    }

    private int getAndIncreaseCount(final String key) {
        if (ScenarioContext.current().get(key) == null) {
            ScenarioContext.current().put(key, 1);
            return 0;
        }
        final Integer value = (Integer) ScenarioContext.current().get(key);
        ScenarioContext.current().put(key, value + 1);

        return value;
    }
}
