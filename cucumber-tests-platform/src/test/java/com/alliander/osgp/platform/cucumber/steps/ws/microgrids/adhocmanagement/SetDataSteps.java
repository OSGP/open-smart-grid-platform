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
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.helpers.SettingsHelper;
import com.alliander.osgp.platform.cucumber.mocks.iec61850.Iec61850MockServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.microgrids.MicrogridsStepsBase;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement.AdHocManagementServiceAdapter;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement.SetDataRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetDataSteps extends MicrogridsStepsBase {

    private static final int NUMBER_OF_INPUTS_FOR_MOCK_VALUE = 3;
    private static final int INDEX_LOGICAL_DEVICE_NAME = 0;
    private static final int INDEX_NODE_NAME = 1;
    private static final int INDEX_NODE_VALUE = 2;

    @Autowired
    private Iec61850MockServer mockServer;

    @Autowired
    private AdHocManagementServiceAdapter adHocManagementServiceAdapter;

    @When("^a set data request is received$")
    public void aSetDataRequestIsReceived(final Map<String, String> requestParameters) throws Throwable {
        final String organizationIdentification = (String) ScenarioContext.Current()
                .get(Keys.KEY_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_ORGANISATION_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.KEY_ORGANISATION_IDENTIFICATION, organizationIdentification);
        final String userName = (String) ScenarioContext.Current().get(Keys.KEY_USER_NAME, Defaults.DEFAULT_USER_NAME);
        ScenarioContext.Current().put(Keys.KEY_USER_NAME, userName);

        final SetDataRequest setDataRequest = SetDataRequestBuilder.fromParameterMap(requestParameters);
        final SetDataAsyncResponse response = this.adHocManagementServiceAdapter.setDataAsync(setDataRequest);

        ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, response.getAsyncResponse().getCorrelationUid());
    }

    @Then("^the set data response should be returned$")
    public void theSetDataResponseShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                Keys.KEY_CORRELATION_UID, correlationUid);

        final SetDataAsyncRequest setDataAsyncRequest = SetDataRequestBuilder.fromParameterMapAsync(extendedParameters);
        final SetDataResponse response = this.adHocManagementServiceAdapter.setData(setDataAsyncRequest);

        final String expectedResult = responseParameters.get(Keys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }

    @Then("^the rtu simulator should contain$")
    public void anRtuSimulatorContaining(final List<List<String>> mockValues) throws Throwable {
        for (final List<String> mockValue : mockValues) {
            if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
                throw new AssertionError("Mock value input rows from the Step DataTable must have "
                        + NUMBER_OF_INPUTS_FOR_MOCK_VALUE + " elements.");
            }
            final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
            final String node = mockValue.get(INDEX_NODE_NAME);
            final String value = mockValue.get(INDEX_NODE_VALUE);           
            assertEquals("Simulator setting", true, (this.mockServer.assertValue(logicalDeviceName, node, value)) );
        }
    }

}
