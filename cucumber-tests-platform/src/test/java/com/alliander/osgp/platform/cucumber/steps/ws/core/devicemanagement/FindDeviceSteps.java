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

import com.alliander.osgp.domain.core.valueobjects.DeviceActivatedFilterType;
import com.alliander.osgp.domain.core.valueobjects.DeviceExternalManagedFilterType;
import com.alliander.osgp.domain.core.valueobjects.DeviceInMaintenanceFilterType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareModuleType;
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
    
    private void fillPropertiesMap(final Map<String, String> requestParameters)
    {
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put("__ORGANISATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
        PROPERTIES_MAP.put("__DEVICE_EXTERNAL_MANAGED__", DeviceExternalManagedFilterType.EXTERNAL_MANAGEMENT.toString());
        PROPERTIES_MAP.put("__DEVICE_ACTIVATED__", DeviceActivatedFilterType.ACTIVE.toString());
        PROPERTIES_MAP.put("__DEVICE_MAINTENANCE__", DeviceInMaintenanceFilterType.ACTIVE.toString());        
        PROPERTIES_MAP.put("__HAS_TECHNICAL_INSTALLATION__", "true");
        PROPERTIES_MAP.put("__FIRMWARE_MODULE_TYPE__", FirmwareModuleType.SECURITY.toString());
        PROPERTIES_MAP.put("__EXACT_MATCH__", "true");
        
        // Page settings
        PROPERTIES_MAP.put("__PAGE_SIZE__", "15");
        PROPERTIES_MAP.put("__PAGE__", "0");
        PROPERTIES_MAP.put("__USE_PAGES__", "false");
    }
    
    @When("receiving a find recent devices request")
    public void receiving_a_find_recent_devices_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
//    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	fillPropertiesMap(requestParameters);
    	
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    @Then("the find recent devices response contains")
    public void the_find_recent_devices_response_contains(final Map<String, String> expectedResponseData) throws Throwable
    {
//    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", expectedResponseData.get(Keys.KEY_DEVICE_IDENTIFICATION));
//        PROPERTIES_MAP.put("__ORGANISATION_IDENTIFICATION__", expectedResponseData.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
//        PROPERTIES_MAP.put("__DEVICE_EXTERNAL_MANAGED__", DeviceExternalManagedFilterType.EXTERNAL_MANAGEMENT.toString());
//        PROPERTIES_MAP.put("__DEVICE_ACTIVATED__", DeviceActivatedFilterType.ACTIVE.toString());
//        PROPERTIES_MAP.put("__DEVICE_MAINTENANCE__", DeviceInMaintenanceFilterType.ACTIVE.toString());        
//        PROPERTIES_MAP.put("__HAS_TECHNICAL_INSTALLATION__", "true");
//        PROPERTIES_MAP.put("__FIRMWARE_MODULE_TYPE__", FirmwareModuleType.SECURITY.toString());
//        PROPERTIES_MAP.put("__EXACT_MATCH__", "true");
    	fillPropertiesMap(expectedResponseData);
        
    	this.waitForResponse(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
}
