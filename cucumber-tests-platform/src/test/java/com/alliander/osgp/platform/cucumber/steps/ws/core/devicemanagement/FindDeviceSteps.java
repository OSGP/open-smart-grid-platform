package com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindDeviceSteps extends CoreStepsBase {

    private static final String TEST_SUITE = "DeviceInstallation";
    private static final String TEST_CASE_NAME = "FindRecentDevices TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "FindRecentDevices";
    
    @When("receiving a find recent devices request")
    public void receiving_a_find_recent_devices_request(final Map<String, String> requestParameters) throws Throwable
    {
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    @Then("the find recent devices response contains")
    public void the_find_recent_devices_response_contains(final Map<String, String> expectedResult) throws Throwable
    {
    	if (this.response.toLowerCase().contains("faultcode")) {
    		ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
    	} else {
        	// check resutl
        	//"<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:FindRecentDevicesResponse xmlns:ns2="http://www.alliander.com/schemas/osgp/deviceinstallation/2014/10" xmlns:ns3="http://www.alliander.com/schemas/osgp/common/2014/10"><ns2:Devices><ns2:DeviceIdentification>TEST1024000000001</ns2:DeviceIdentification><ns2:Alias/><ns2:ContainerPostalCode/><ns2:ContainerCity/><ns2:ContainerStreet/><ns2:ContainerNumber/><ns2:ContainerMunicipality/><ns2:GpsLatitude>0.0</ns2:GpsLatitude><ns2:GpsLongitude>0.0</ns2:GpsLongitude><ns2:Activated>true</ns2:Activated><ns2:HasSchedule>false</ns2:HasSchedule></ns2:Devices></ns2:FindRecentDevicesResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>"
        	Assert.fail("TODO: Check find recent devices response");
    	}
    }
}
