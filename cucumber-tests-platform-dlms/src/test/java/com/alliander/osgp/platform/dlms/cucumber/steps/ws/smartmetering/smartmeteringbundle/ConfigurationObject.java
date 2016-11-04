/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationObject extends SmartMeteringStepsBase {

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML_501 = "501 Retrieve specific attribute value bundle";
    private static final String TEST_CASE_XML_526 = "526 Retrieve association objectlist bundle";

    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetBundleResponse - Request 1";

    @When("^a retrieve configuration request received as part of a bundled request$")
    public void aRetrieveConfigurationRequestIsReceivedAsPartOfABundledRequest(final Map<String, String> settings)
            throws Throwable {
        this.setDeviceAndOrganisationProperties(settings);
        PROPERTIES_MAP.put("ObisCodeA", getString(settings, "ObisCodeA", "0"));
        PROPERTIES_MAP.put("ObisCodeB", getString(settings, "ObisCodeB", "0"));
        PROPERTIES_MAP.put("ObisCodeC", getString(settings, "ObisCodeC", "0"));
        PROPERTIES_MAP.put("ObisCodeD", getString(settings, "ObisCodeD", "0"));
        PROPERTIES_MAP.put("ObisCodeE", getString(settings, "ObisCodeE", "0"));
        PROPERTIES_MAP.put("ObisCodeF", getString(settings, "ObisCodeF", "0"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_501, TEST_SUITE_XML);
    }

    @When("^the get associationLnObjects request is received as part of a bundled request$")
    public void theGetAssociationLnObjectsRequestIsReceivedAsPartOfABundledRequest(final Map<String, String> settings)
            throws Throwable {
        this.setDeviceAndOrganisationProperties(settings);

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_526, TEST_SUITE_XML);
    }

    private void setDeviceAndOrganisationProperties(final Map<String, String> settings) {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^the retrieve configuration response contains$")
    public void isPartOfTheResponse(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML_501,
                TEST_SUITE_XML);

        assertTrue(this.response.contains(settings.get("ResponsePart")));
    }

}
