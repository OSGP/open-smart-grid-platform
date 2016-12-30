package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RemoveDeviceSteps {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a remove device request to the Platform.
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove device request$")
    public void receiving_a_remove_device_request(final Map<String, String> requestSettings) throws Throwable {
        final RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.removeDevice(request));
        } catch (final SoapFaultClientException ex) {
            LoggerFactory.getLogger(RemoveDeviceSteps.class).info("Response: " + ex);
            // ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @throws Throwable
     */
    @Then("^the remove device response is successfull$")
    public void the_remove_device_response_is_successfull() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof RemoveDeviceResponse);
    }

}