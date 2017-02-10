/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.microgrids.adhocmanagement;

import static org.junit.Assert.fail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement.GetDataRequestBuilder;

import cucumber.api.java.en.Then;

public class FaultSteps extends GlueBase {

    @Autowired
    private AdHocManagementClient client;

    @Then("^a SOAP fault should be returned$")
    public void aSoapFaultShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                Keys.KEY_CORRELATION_UID, correlationUid);

        final GetDataAsyncRequest getDataAsyncRequest = GetDataRequestBuilder.fromParameterMapAsync(extendedParameters);

        try {
            final GetDataResponse response = this.client.getData(getDataAsyncRequest);
            fail("Expected a SOAP fault, but got a GetDataResponse with result " + response.getResult().value() + ".");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.RESPONSE, e);
        }

        GenericResponseSteps.verifySoapFault(responseParameters);
    }
}
