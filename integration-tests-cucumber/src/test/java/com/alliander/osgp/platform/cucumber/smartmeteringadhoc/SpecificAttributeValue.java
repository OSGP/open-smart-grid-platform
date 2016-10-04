package com.alliander.osgp.platform.cucumber.smartmeteringadhoc;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SpecificAttributeValue extends SmartMetering {
    private static final String PATH_RESULT_CONFIGURATION_DATA = "/Envelope/Body/SpecificAttributeValueResponse/ConfigurationData/text()";

    private static final String XPATH_MATCHER_RESULT_CONFIGURATION_DATA = "DataObject: Choice=\\w[A-Z]+, ResultData \\w+, value=";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "534 Retrieve specific attribute value";
    private static final String TEST_CASE_NAME_REQUEST = "GetSpecificAttributeValue - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetSpecificAttributeValueResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecificAttributeValue.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^the retrieve specific attribute value request is received$")
    public void theGetRetrieveSpecificConfigurationRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the specific attribute value item should be returned$")
    public void theSpecificConfigurationItemShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONFIGURATION_DATA,
                XPATH_MATCHER_RESULT_CONFIGURATION_DATA));
    }
}
