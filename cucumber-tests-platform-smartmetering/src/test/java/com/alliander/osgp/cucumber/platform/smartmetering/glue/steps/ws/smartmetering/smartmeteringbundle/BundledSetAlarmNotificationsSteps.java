package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetAlarmNotificationsRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledSetAlarmNotificationsSteps extends BaseBundleSteps {
    @When("^the bundle request contains a set alarm notifications action$")
    public void theBundleRequestContainsASetAlarmNotificationsAction() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final SetAlarmNotificationsRequest action = new SetAlarmNotificationsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(request, action);
    }

    @When("^the bundle request contains a set alarm notifications action with parameters$")
    public void theBundleRequestContainsASetAlarmNotificationsAction(final Map<String, String> parameters)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final SetAlarmNotificationsRequest action = new SetAlarmNotificationsRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a set alarm notifications response$")
    public void theBundleResponseShouldContainASetAlarmNotificationsResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a set alarm notifications response with values$")
    public void theBundleResponseShouldContainASetAlarmNotificationsResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }

}
