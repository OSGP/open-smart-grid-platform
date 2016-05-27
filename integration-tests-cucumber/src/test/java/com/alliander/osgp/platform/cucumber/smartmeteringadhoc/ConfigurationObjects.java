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

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationObjects extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/RetrieveConfigurationObjectsResponse/Result/text()";
    private static final String PATH_RESULT_OUTPUT = "/Envelope/Body/RetrieveConfigurationObjectsResponse/Output/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";
    private static final String XPATH_MATCHER_RESULT_OUTPUT = "DataObject: Choice=\\w[A-Z]+\\(\\d+\\), ResultData \\w+, value=";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "193 Retrieve available objects of a meter";
    private static final String TEST_CASE_NAME_REQUEST = "RetrieveConfigurationObjects - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetRetrieveConfigurationObjectsResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationObjects.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the retrieve configuration request is received$")
    public void theGetRetrieveConfigurationRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION, this.organisationId.getOrganisationId());

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^all the configuration items should be returned$")
    public void allTheConfigurationItemsShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID, this.correlationUid);

        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_OUTPUT, XPATH_MATCHER_RESULT_OUTPUT));
    }
}
