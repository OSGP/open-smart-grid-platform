/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ProfileGenericDataResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Action;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundleSteps extends BaseBundleSteps {

    private static Map<Class<?>, Class<?>> REQUEST_RESPONSE_MAP = new HashMap<>();
    static {
        REQUEST_RESPONSE_MAP.put(FindEventsRequest.class, FindEventsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetActualMeterReadsRequest.class, ActualMeterReadsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAdministrativeStatusRequest.class, AdministrativeStatusResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAllAttributeValuesRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAssociationLnObjectsRequest.class, AssociationLnObjectsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetConfigurationObjectRequest.class, GetConfigurationObjectResponse.class);
        REQUEST_RESPONSE_MAP.put(GetPeriodicMeterReadsRequest.class, PeriodicMeterReadsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetProfileGenericDataRequest.class, ProfileGenericDataResponse.class);
        REQUEST_RESPONSE_MAP.put(GetSpecificAttributeValueRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(ReadAlarmRegisterRequest.class, ReadAlarmRegisterResponse.class);
        REQUEST_RESPONSE_MAP.put(SetActivityCalendarRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetAdministrativeStatusRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetAlarmNotificationsRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetClockConfigurationRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetConfigurationObjectRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetPushSetupAlarmRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetSpecialDaysRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SynchronizeTimeRequest.class, ActionResponse.class);
    }

    @Autowired
    private SmartMeteringBundleClient client;

    @Given("^a bundle request$")
    public void aABundleRequest(final Map<String, String> settings) throws Throwable {
        final BundleRequest request = new BundleRequest();
        request.setDeviceIdentification(getString(settings, PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
                PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        final Actions actions = new Actions();
        request.setActions(actions);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.BUNDLE_REQUEST, request);
    }

    @When("^the bundle request is received$")
    public void theBundleRequestIsReceived() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final BundleAsyncResponse asyncResponse = this.client.sendBundleRequest(request);

        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    @Then("^the number of responses in the bundle response should match the number of actions in the bundle request$")
    public void theNumberOfResponsesInTheBundleResponseShouldMatchTheNumberOfActionsInTheBundleRequest()
            throws Throwable {

        this.ensureBundleResponse();

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
        final BundleResponse response = (BundleResponse) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

        this.assertSameSize(request, response);
    }

    @Then("^the order of the responses in the bundle response should match the order of actions in the bundle request$")
    public void theOrderOfTheResponsesInTheBundleResponseShouldMatchTheOrderOfActionsInTheBundleRequest()
            throws Throwable {

        this.ensureBundleResponse();

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
        final BundleResponse response = (BundleResponse) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

        this.assertSameSize(request, response);
        this.assertSameOrder(request, response);
    }

    private void assertSameSize(final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

        final int actionsSize = bundleRequest.getActions().getActionList().size();
        final int responsesSize = bundleResponse.getAllResponses().getResponseList().size();

        assertEquals(
                "The number of responses in the bundle responses should match the number of actions in the bundle request",
                actionsSize, responsesSize);

    }

    private void assertSameOrder(final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

        final int actionsSize = bundleRequest.getActions().getActionList().size();

        for (int i = 0; i < actionsSize; i++) {
            final Action action = bundleRequest.getActions().getActionList().get(i);
            final Response response = bundleResponse.getAllResponses().getResponseList().get(i);

            assertEquals(REQUEST_RESPONSE_MAP.get(action.getClass()), response.getClass());
        }

    }
}
