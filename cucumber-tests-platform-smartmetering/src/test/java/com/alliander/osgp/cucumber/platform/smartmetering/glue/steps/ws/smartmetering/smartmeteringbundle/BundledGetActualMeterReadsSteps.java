package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetActualMeterReadsSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get actual meter reads action$")
    public void theBundleRequestContainsAGetActualMeterReadsAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetActualMeterReadsRequest action = new GetActualMeterReadsRequest();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get actual meter reads response$")
    public void theBundleResponseShouldContainAGetActualMeterReadsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActualMeterReadsResponse);
    }

    @Then("^the bundle response should contain a get actual meter reads response with values$")
    public void theBundleResponseShouldContainAGetActualMeterReadsResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActualMeterReadsResponse);
    }

}
