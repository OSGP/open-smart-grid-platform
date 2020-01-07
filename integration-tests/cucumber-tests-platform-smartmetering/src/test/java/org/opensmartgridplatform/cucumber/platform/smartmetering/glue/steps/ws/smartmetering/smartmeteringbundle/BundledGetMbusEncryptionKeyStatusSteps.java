/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetMbusEncryptionKeyStatusRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetMbusEncryptionKeyStatusSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get M-Bus encryption key status action$")
    public void theBundleRequestContainsAGetMbusEncryptionKeyStatusAction() throws Throwable {

        final GetMbusEncryptionKeyStatusRequest action = new GetMbusEncryptionKeyStatusRequestBuilder().withDefaults()
                .build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a get M-Bus encryption key status action with parameters$")
    public void theBundleRequestContainsAGetMbusEncryptionKeyStatusAction(final Map<String, String> parameters)
            throws Throwable {

        final GetMbusEncryptionKeyStatusRequest action = new GetMbusEncryptionKeyStatusRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get M-Bus encryption key status response$")
    public void theBundleResponseShouldContainAGetMbusEncryptionKeyStatusResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertThat(response instanceof GetMbusEncryptionKeyStatusResponse).as("Not a valid response").isTrue();
        assertThat(((GetMbusEncryptionKeyStatusResponse) response).getEncryptionKeyStatus())
                .as("Encryption Key Status should not be null.").isNotNull();
        assertThat(((GetMbusEncryptionKeyStatusResponse) response).getMbusDeviceIdentification())
                .as("M-Bus Device Identification should not be null.").isNotNull();
    }
}
