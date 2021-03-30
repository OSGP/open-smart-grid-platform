/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetModemInfoRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class BundledGetModemInfoObjectSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get modem info action$")
    public void theBundleRequestContainsAGetModemInfoAction() throws Throwable {

        final GetModemInfoRequest action = new GetModemInfoRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get modem info response$")
    public void theBundleResponseShouldContainAGetModemInfoResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    }

    @Then("^the bundle response should contain a get modem info response with values$")
    public void theBundleResponseShouldContainAGetModemInfoResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
        assertThat(response.getResult().name()).as("Result is not as expected.")
                .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    }

}
