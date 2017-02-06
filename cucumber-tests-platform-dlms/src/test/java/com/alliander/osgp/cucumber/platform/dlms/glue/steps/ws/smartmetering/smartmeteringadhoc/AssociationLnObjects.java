/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AssociationLnObjects extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/GetAssociationLnObjectsResponse/Result/text()";
    private static final String PATH_RESULT_CLASSID = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/ClassId/text()";
    private static final String PATH_RESULT_VERSION = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/Version/text()";
    private static final String PATH_RESULT_LOGICALNAME = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/LogicalName/text()";
    private static final String PATH_RESULT_ATTRIBUTE_ID = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/AccessRights/AttributeAccess/AttributeAccessItem/AttributeId/text()";
    private static final String PATH_RESULT_ACCESS_MODE = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/AccessRights/AttributeAccess/AttributeAccessItem/AccessMode/text()";

    private static final String XPATH_MATCHER_RESULT_DECIMAL = "\\d+";
    private static final String XPATH_MATCHER_RESULT_ACCESS_MODE = "\\w+\\_\\w+";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "517 Retrieve association objectlist";
    private static final String TEST_CASE_NAME_REQUEST = "GetAssociationLnObjects - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE_REQUEST = "GetGetAssociationLnObjectsResponse - Request 1";

    @When("^receiving a retrieve association LN objectlist request$")
    public void receivingARetrieveAssociationLNObjectlistRequest(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the objectlist should be returned$")
    public void theObjectlistShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CLASSID, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_VERSION, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult
                .assertXpath(this.response, PATH_RESULT_LOGICALNAME, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ATTRIBUTE_ID,
                XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACCESS_MODE,
                XPATH_MATCHER_RESULT_ACCESS_MODE));
    }
}
