package com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

public class FindDeviceSteps extends CoreStepsBase {

	@Autowired
    private MockOslpServer oslpMockServer;
	
    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "FindDevices TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "FindDevices";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FindDeviceSteps.class);
    
    @When("receiving a find recent devices request")
    public void receiving_a_find_recent_devices_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    @Then("the find recent devices response contains")
    public void the_find_recent_devices_response_contains(final Map<String, String> expectedResponseData) throws Throwable
    {
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", expectedResponseData.get(Keys.KEY_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put("__ORGANISATION_IDENTIFICATION__", expectedResponseData.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
        
//    	this.waitForResponse(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME, TEST_CASE_NAME, TEST_SUITE);
    }
}
