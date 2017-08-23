package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetConfigurationObjectRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledSetConfigurationObjectSteps extends BaseBundleSteps {

    @When("^the bundle request contains a set configuration object action$")
    public void theBundleRequestContainsASetConfigurationObjectAction() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final SetConfigurationObjectRequest action = new SetConfigurationObjectRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(request, action);
    }

    @When("^the bundle request contains a set configuration object action with parameters$")
    public void theBundleRequestContainsASetConfigurationObjectAction(final Map<String, String> parameters)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final SetConfigurationObjectRequest action = new SetConfigurationObjectRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a set configuration object response$")
    public void theBundleResponseShouldContainASetConfigurationObjectResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a set configuration object response with values$")
    public void theBundleResponseShouldContainASetConfigurationObjectResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }

}
