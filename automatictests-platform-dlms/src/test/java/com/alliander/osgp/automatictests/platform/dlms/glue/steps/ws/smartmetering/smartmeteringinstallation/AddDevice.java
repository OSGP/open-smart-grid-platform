/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.dlms.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;
import static com.alliander.osgp.automatictests.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetAddDeviceResponse - Request 1";

    @When("^receiving an smartmetering add device request$")
    public void receiving_an_smartmetering_add_device_request(final Map<String, String> setings) throws Throwable {
        PROPERTIES_MAP.put(Keys.DEVICE_IDENTIFICATION,
                getString(setings, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.DEVICE_TYPE, getString(setings, Keys.DEVICE_TYPE, Defaults.DEVICE_TYPE));
        PROPERTIES_MAP.put(Keys.DEVICE_COMMUNICATIONMETHOD, setings.get(Keys.DEVICE_COMMUNICATIONMETHOD));
        PROPERTIES_MAP.put(Keys.DEVICE_COMMUNICATIONPROVIDER, setings.get(Keys.DEVICE_COMMUNICATIONPROVIDER));
        PROPERTIES_MAP.put(Keys.DEVICE_ICCID, setings.get(Keys.DEVICE_ICCID));
        PROPERTIES_MAP.put(Keys.DEVICE_DSMRVERSION, setings.get(Keys.DEVICE_DSMRVERSION));
        PROPERTIES_MAP.put(Keys.DEVICE_SUPPLIER, setings.get(Keys.DEVICE_SUPPLIER));
        PROPERTIES_MAP.put(Keys.DEVICE_HLS3ACTIVE, setings.get(Keys.DEVICE_HLS3ACTIVE));
        PROPERTIES_MAP.put(Keys.DEVICE_HLS4ACTIVE, setings.get(Keys.DEVICE_HLS4ACTIVE));
        PROPERTIES_MAP.put(Keys.DEVICE_HLS5ACTIVE, setings.get(Keys.DEVICE_HLS5ACTIVE));
        PROPERTIES_MAP.put(Keys.DEVICE_MASTERKEY, setings.get(Keys.DEVICE_MASTERKEY));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the smartmetering add device response contains$")
    public void the_add_device_request_contains(final Map<String, String> settings) throws Throwable {
        this.runXpathResult
                .assertXpath(
                        this.response,
                        PATH_DEVICE_IDENTIFICATION,
                        getString(settings, Keys.ORGANIZATION_IDENTIFICATION,
                                Defaults.ORGANIZATION_IDENTIFICATION));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(settings, "OrganizationIdentification", Defaults.ORGANIZATION_IDENTIFICATION));
    }

    @Then("^receiving an get add device response request$")
    public void receiving_an_get_add_device_response_request(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP
                .put(Keys.CORRELATION_UID, ScenarioContext.Current().get(Keys.CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);
    }

    @Then("^the get add device request response should be ok$")
    public void the_get_add_device_request_response_should_be_ok() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }
}
