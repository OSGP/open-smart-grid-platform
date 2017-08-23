package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetPeriodicMeterReadsRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledGetPeriodicMeterReadsSteps extends BaseBundleSteps {

    @When("^the bundle request contains a get periodic meter reads action$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetPeriodicMeterReadsRequest action = new GetPeriodicMeterReadsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(request, action);
    }

    @When("^the bundle request contains a get periodic meter reads action with parameters$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsActionWithParameters(final Map<String, String> parameters)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetPeriodicMeterReadsRequest action = new GetPeriodicMeterReadsRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get periodic meter reads response$")
    public void theBundleResponseShouldContainAGetPeriodicMeterReadsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof PeriodicMeterReadsResponse);
    }

    @Then("^the bundle response should contain a get periodic meter reads response with values$")
    public void theBundleResponseShouldContainAGetPeriodicMeterReadsResponseWithValues(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        // TODO [SLIM-1095] Implement correct assertions using expected values
        assertTrue("Not a valid response", response instanceof PeriodicMeterReadsResponse);
    }

}
