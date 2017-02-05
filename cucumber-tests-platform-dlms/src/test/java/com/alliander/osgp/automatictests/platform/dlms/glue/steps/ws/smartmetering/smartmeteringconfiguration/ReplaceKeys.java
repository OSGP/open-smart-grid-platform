/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.dlms.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReplaceKeys extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/ReplaceKeysResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "128/441 Replace Keys";
    private static final String TEST_CASE_NAME_REQUEST = "ReplaceKeys - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetReplaceKeysResponse - Request 1";

    @When("^the replace keys request is received$")
    public void theReplaceKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.DEVICE_IDENTIFICATION,
                getString(settings, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.ORGANIZATION_IDENTIFICATION,
                        getString(settings, Keys.ORGANIZATION_IDENTIFICATION,
                                Defaults.ORGANIZATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the new keys are set on the device$")
    public void theNewKeysAreSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.DEVICE_IDENTIFICATION,
                getString(settings, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.CORRELATION_UID, ScenarioContext.Current().get(Keys.CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }

    @And("^the new keys are stored in the osgp_adapter_protocol_dlms database security_key table$")
    public void theNewKeysAreStoredInTheOsgpAdapterProtocolDlmsDatabaseSecurityKeyTable() throws Throwable {
        // TODO we have to implement this method.
    }
}
