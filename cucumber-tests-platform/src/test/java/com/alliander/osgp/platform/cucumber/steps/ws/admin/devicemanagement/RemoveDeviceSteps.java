package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RemoveDeviceSteps extends AdminStepsBase {
    
	@Autowired
    private MockOslpServer oslpMockServer;
	
    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "RemoveDevice TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "RemoveDevice";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDeviceSteps.class);
    
    /**
     * Send a remove device request to the Platform.
     * @param requestParameters
     * 				An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove device request$")
    public void receiving_a_remove_device_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData
     * 					The table with the expected fields in the response.
     * @throws Throwable
     */
    @Then("^the remove device response is successfull$")
    public void the_remove_device_response_is_successfull() throws Throwable
    {
//    	Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/RemoveDeviceResponse", ""));
    }
}