/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.microgrids.adhocmanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.helpers.SettingsHelper;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.FaultDetailElement;
import com.alliander.osgp.platform.cucumber.support.ws.SoapFaultHelper;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.AdHocManagementClient;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.GetDataRequestBuilder;

import cucumber.api.java.en.Then;

public class FaultSteps {

    @Autowired
    private AdHocManagementClient client;

    @Then("^a SOAP fault should be returned$")
    public void aSoapFaultShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                Keys.KEY_CORRELATION_UID, correlationUid);

        final GetDataAsyncRequest getDataAsyncRequest = GetDataRequestBuilder.fromParameterMapAsync(extendedParameters);

        SoapFaultClientException soapFaultClientException = null;
        try {
            final GetDataResponse response = this.client.getData(getDataAsyncRequest);
            fail("Expected a SOAP fault, but got a GetDataResponse with result " + response.getResult().value() + ".");
        } catch (final SoapFaultClientException e) {
            soapFaultClientException = e;
        }

        final Map<FaultDetailElement, String> faultDetailValuesByElement = SoapFaultHelper
                .getFaultDetailValuesByElement(soapFaultClientException);

        this.assertFaultDetails(responseParameters, faultDetailValuesByElement);
    }

    private void assertFaultDetails(final Map<String, String> expected, final Map<FaultDetailElement, String> actual) {

        for (final Map.Entry<String, String> expectedEntry : expected.entrySet()) {
            final String localName = expectedEntry.getKey();
            final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(localName);
            if (faultDetailElement == null) {
                /*
                 * Specified response parameter is not a FaultDetailElement
                 * (e.g. DeviceIdentification), skip it for the assertions.
                 */
                continue;
            }
            final String expectedValue = expectedEntry.getValue();
            final String actualValue = actual.get(faultDetailElement);
            assertEquals(localName, expectedValue, actualValue);
        }
    }
}
