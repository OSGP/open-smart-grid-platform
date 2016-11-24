package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement.SetEventNotificationsSteps;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

public class StartDeviceSteps extends CoreStepsBase {
    
	@Autowired
    private MockOslpServer oslpMockServer;
	
    private static final String TEST_SUITE = "DeviceInstallation";
    private static final String TEST_CASE_NAME = "StartDeviceTest TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "StartDeviceTest";
    
    private static final String TEST_RESPONSE_CASE_NAME = "GetStartDeviceTestResponse TestCase";
    private static final String TEST_CASE_NAME_RESPONSE = "GetStartDeviceTestResponse";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StartDeviceSteps.class);
    
    /**
     * 
     * @param requestParameters
     * @throws Throwable
     */
    @When("receiving a start device request")
    public void receiving_a_start_device_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	
//    	this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    /**
     * 
     * @param expectedResponseData
     * @throws Throwable
     */
    @Then("the start device async response contains")
    public void the_start_device_async_response_contains(final Map<String, String> expectedResponseData) throws Throwable
    {
    	this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION,
                getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }
    
    /**
     * 
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("the platform buffers a start device response message for device \"([^\"]*)\"")
    public void the_platform_buffers_a_start_device_response_message_for_device(final String deviceIdentification) throws Throwable
    {
    	// Required parameters
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", deviceIdentification);
        PROPERTIES_MAP.put("__CORRELATION_UID__", (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        
    	this.waitForResponse(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE,
    			TEST_RESPONSE_CASE_NAME, TEST_SUITE);
    }
}
