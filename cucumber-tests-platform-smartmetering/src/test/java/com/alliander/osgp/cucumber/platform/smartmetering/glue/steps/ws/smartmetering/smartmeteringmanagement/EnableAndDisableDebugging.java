/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.DisableDebuggingRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.EnableDebuggingRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EnableAndDisableDebugging extends SmartMeteringStepsBase {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private SmartMeteringManagementRequestClient<EnableDebuggingAsyncResponse, EnableDebuggingRequest> smartMeteringManagementRequestClientEnableDebugging;

    @Autowired
    private SmartMeteringManagementResponseClient<EnableDebuggingResponse, EnableDebuggingAsyncRequest> smartMeteringManagementResponseClientEnableDebugging;

    @Autowired
    private SmartMeteringManagementRequestClient<DisableDebuggingAsyncResponse, DisableDebuggingRequest> smartMeteringManagementRequestClientDisableDebugging;

    @Autowired
    private SmartMeteringManagementResponseClient<DisableDebuggingResponse, DisableDebuggingAsyncRequest> smartMeteringManagementResponseClientDisableDebugging;

    @When("^the enable Debug request is received$")
    public void theEnableDebugRequestIsReceived(final Map<String, String> requestData) throws Throwable {

        final EnableDebuggingRequest enableDebuggingRequest = EnableDebuggingRequestFactory
                .fromParameterMap(requestData);
        final EnableDebuggingAsyncResponse enableDebuggingAsyncResponse = this.smartMeteringManagementRequestClientEnableDebugging
                .doRequest(enableDebuggingRequest);

        assertNotNull("AsyncResponse should not be null", enableDebuggingAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                enableDebuggingAsyncResponse.getCorrelationUid());
    }

    @Then("^the device debug information should be enabled$")
    public void theDeviceDebugInformationShouldBeEnabled() throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(
                ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString());

        assertTrue("Debug mode", device.isInDebugMode());
    }

    @Then("^the enable debug response should be \"([^\"]*)\"$")
    public void theEnableDebugResponseShouldBe(final String result) throws Throwable {
        final EnableDebuggingAsyncRequest enableDebuggingAsyncRequest = EnableDebuggingRequestFactory
                .fromScenarioContext();
        final EnableDebuggingResponse enableDebuggingResponse = this.smartMeteringManagementResponseClientEnableDebugging
                .getResponse(enableDebuggingAsyncRequest);

        assertNotNull("EnableDebugRequestResponse should not be null", enableDebuggingResponse);
        assertNotNull("Expected results", enableDebuggingResponse.getResult());
    }

    @When("^the disable Debug request is received$")
    public void theDisableDebugRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final DisableDebuggingRequest disableDebuggingRequest = DisableDebuggingRequestFactory
                .fromParameterMap(requestData);
        final DisableDebuggingAsyncResponse disableDebuggingAsyncResponse = this.smartMeteringManagementRequestClientDisableDebugging
                .doRequest(disableDebuggingRequest);

        assertNotNull("AsyncResponse should not be null", disableDebuggingAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                disableDebuggingAsyncResponse.getCorrelationUid());
    }

    @Then("^the device debug information should be disabled$")
    public void theDeviceDebugInformationShouldBeDisabled() throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(
                ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString());

        assertFalse("Debug mode", device.isInDebugMode());
    }

    @Then("^the disable debug response should be \"([^\"]*)\"$")
    public void theDisableDebugResponseShouldBe(final String result) throws Throwable {
        final DisableDebuggingAsyncRequest disableDebuggingAsyncRequest = DisableDebuggingRequestFactory
                .fromScenarioContext();
        final DisableDebuggingResponse disableDebuggingResponse = this.smartMeteringManagementResponseClientDisableDebugging
                .getResponse(disableDebuggingAsyncRequest);

        assertNotNull("DisableDebugRequestResponse should not be null", disableDebuggingResponse);
        assertNotNull("Expected result", disableDebuggingResponse.getResult());
    }
}
