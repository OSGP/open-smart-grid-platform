package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.alliander.osgp.platform.cucumber.support.ws.core.deviceinstallation.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindRecentDeviceSteps extends CoreStepsBase {

	@Autowired
	private CoreDeviceInstallationClient client;

	@When("receiving a find recent devices request")
    public void receiving_a_find_recent_devices_request(final Map<String, String> requestParameters) throws Throwable
    {
		FindRecentDevicesRequest request = new FindRecentDevicesRequest();
		
		try {
            ScenarioContext.Current().put(Keys.RESPONSE, client.findRecentDevices(request));
        } catch(SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }
    
    @Then("the find recent devices response contains \"([^\"]*)\" devices")
    public void the_find_recent_devices_response_contains(final Integer numberOfDevices)
    {
    	FindRecentDevicesResponse response = (FindRecentDevicesResponse)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertEquals((int)numberOfDevices, response.getDevices().size());
    }
    
	@Then("the find recent devices response contains at index \"([^\"]*)\"")
    public void the_find_recent_devices_response_contains_at_index(final Integer index, final Map<String, String> expectedDevice) throws Throwable
    {
    	FindRecentDevicesResponse response = (FindRecentDevicesResponse)ScenarioContext.Current().get(Keys.RESPONSE);

    	DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(index - 1));
    }
}
