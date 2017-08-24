package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetAdministrativeStatusSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get administrative status action$")
    public void theBundleRequestContainsAGetAdministrativeStatusAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetAdministrativeStatusRequest action = new GetAdministrativeStatusRequest();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get administrative status response$")
    public void theBundleResponseShouldContainAGetAdministrativeStatusResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AdministrativeStatusResponse);
    }

    @Then("^the bundle response should contain a get administrative status response with values$")
    public void theBundleResponseShouldContainAGetAdministrativeStatusResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AdministrativeStatusResponse);
    }

}
