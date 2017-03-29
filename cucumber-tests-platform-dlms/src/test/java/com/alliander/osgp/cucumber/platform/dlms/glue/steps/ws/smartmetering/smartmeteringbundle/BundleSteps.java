package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.builders.BundleRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.GetProfileGenericDataRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.SmartMeteringBundleManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundleSteps {
    @Autowired
    private SmartMeteringBundleManagementClient client;

    @When("a get generic profile data request is received as part of a bundled request")
    public void whenAGetGenericProfileDataBundleRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final GetProfileGenericDataRequest action = new GetProfileGenericDataRequestBuilder().fromParameterMap(settings)
                .build();

        final Actions actions = new Actions();
        actions.getActionList().add(action);

        final BundleRequest request = new BundleRequestBuilder()
                .withDeviceIdentification(settings.get(Keys.DEVICE_IDENTIFICATION)).withActions(actions).build();
        final BundleAsyncResponse response = this.client.sendBundleRequest(request);

        ScenarioContext.Current().put(Keys.CORRELATION_UID, response.getCorrelationUid());
        ScenarioContext.Current().put(Keys.DEVICE_IDENTIFICATION, response.getDeviceIdentification());
    }

    @Then("the profile generic data should be part of the bundle response")
    public void thenTheProfileGenericDataShouldBePartOfTheBundleResponse(final Map<String, String> settings) {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.CORRELATION_UID);
        final String deviceIdentification = (String) ScenarioContext.Current().get(Keys.DEVICE_IDENTIFICATION);

        final BundleAsyncRequest request = new BundleAsyncRequest();
        request.setCorrelationUid(correlationUid);
        request.setDeviceIdentification(deviceIdentification);

    }
}
