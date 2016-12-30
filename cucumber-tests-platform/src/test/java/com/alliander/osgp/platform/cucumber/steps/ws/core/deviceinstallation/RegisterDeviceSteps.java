package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.RegisterDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.RegisterDeviceResponse;
import com.alliander.osgp.platform.cucumber.config.CorePersistenceConfig;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RegisterDeviceSteps {

    @Autowired
    private CorePersistenceConfig configuration;

    @Autowired
    private CoreDeviceInstallationClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceSteps.class);

    // @Given("^a not registered device$")
    // public void a_not_registered_device(final Map<String, String>
    // requestParameters) {
    //
    // }

    @When("^receiving a register device request$")
    public void receiving_a_register_device_request(final Map<String, String> requestParameters) throws Throwable {
        final RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getRegisterDeviceResponse(request));
            // ScenarioContext.Current().put(Keys.RESPONSE,
            // this.client.getRegisterDeviceResponse(request));
            // } catch (final SoapFaultClientException ex) {
        } catch (final Throwable ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the register device response contains$")
    public void the_register_device_async_response_contains(final Map<String, String> expectedDevice) throws Throwable {
        final RegisterDeviceResponse response = (RegisterDeviceResponse) ScenarioContext.Current().get(Keys.RESPONSE);

        System.out.println();
        DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(0));
    }

    // @When("receiving a find recent devices request")
    // public void receiving_a_find_recent_devices_request(final Map<String,
    // String> requestParameters) throws Throwable {
    // final FindRecentDevicesRequest request = new FindRecentDevicesRequest();
    //
    // try {
    // ScenarioContext.Current().put(Keys.RESPONSE,
    // this.client.findRecentDevices(request));
    // } catch (final SoapFaultClientException ex) {
    // ScenarioContext.Current().put(Keys.RESPONSE, ex);
    // }
    // }
    //
    // @Then("the find recent devices response contains \"([^\"]*)\" devices")
    // public void the_find_recent_devices_response_contains(final Integer
    // numberOfDevices) {
    // final FindRecentDevicesResponse response = (FindRecentDevicesResponse)
    // ScenarioContext.Current()
    // .get(Keys.RESPONSE);
    //
    // Assert.assertEquals((int) numberOfDevices, response.getDevices().size());
    // }
    //
    // @Then("the find recent devices response contains at index \"([^\"]*)\"")
    // public void the_find_recent_devices_response_contains_at_index(final
    // Integer index,
    // final Map<String, String> expectedDevice) throws Throwable {
    // final FindRecentDevicesResponse response = (FindRecentDevicesResponse)
    // ScenarioContext.Current()
    // .get(Keys.RESPONSE);
    //
    // DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(index -
    // 1));
    // }
}
