/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationObject extends SmartMeteringStepsBase {

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML_501 = "501 Retrieve specific configuration object bundle";
    private static final String TEST_CASE_XML_526 = "526 Retrieve association objectlist bundle";

    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetBundleResponse - Request 1";

    @When("^a retrieve configuration request for OBIS code (\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+) is received as part of a bundled request$")
    public void aRetrieveConfigurationRequestForOBISCodeIsReceivedAsPartOfABundledRequest(final int obisCodeA,
            final int obisCodeB, final int obisCodeC, final int obisCodeD, final int obisCodeE, final int obisCodeF)
            throws Throwable {
        this.setDeviceAndOrganisationProperties();
        PROPERTIES_MAP.put("ObisCodeA", Integer.toString(obisCodeA));
        PROPERTIES_MAP.put("ObisCodeB", Integer.toString(obisCodeB));
        PROPERTIES_MAP.put("ObisCodeC", Integer.toString(obisCodeC));
        PROPERTIES_MAP.put("ObisCodeD", Integer.toString(obisCodeD));
        PROPERTIES_MAP.put("ObisCodeE", Integer.toString(obisCodeE));
        PROPERTIES_MAP.put("ObisCodeF", Integer.toString(obisCodeF));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_501, TEST_SUITE_XML);
    }

    @When("^the get associationLnObjects request is received as part of a bundled request$")
    public void theGetAssociationLnObjectsRequestIsReceivedAsPartOfABundledRequest() throws Throwable {
        this.setDeviceAndOrganisationProperties();
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_526, TEST_SUITE_XML);
    }

    private void setDeviceAndOrganisationProperties() {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, ScenarioContext.Current().get("DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, ScenarioContext.Current().get("OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toString());
    }

    @Then("^\"([^\"]*)\" is part of the response$")
    public void isPartOfTheResponse(final String responsePart) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML_501, TEST_SUITE_XML);

        assertTrue(this.response.contains(responsePart));
    }

}
