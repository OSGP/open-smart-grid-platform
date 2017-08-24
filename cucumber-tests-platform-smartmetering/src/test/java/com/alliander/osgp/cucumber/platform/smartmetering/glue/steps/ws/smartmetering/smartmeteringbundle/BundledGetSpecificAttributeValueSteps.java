package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetSpecificAttributeValueRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetSpecificAttributeValueSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get specific attribute value action$")
    public void theBundleRequestContainsAGetSpecificAttributeValueAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetSpecificAttributeValueRequest action = new GetSpecificAttributeValueRequestBuilder().withDefaults()
                .build();

        this.addActionToBundleRequest(request, action);
    }

    @Given("^the bundle request contains a get specific attribute value action with parameters$")
    public void theBundleRequestContainsAGetSpecificAttributeValueAction(final Map<String, String> parameters)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetSpecificAttributeValueRequest action = new GetSpecificAttributeValueRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get specific attribute value response$")
    public void theBundleResponseShouldContainAGetSpecificAttributeValueResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a get specific attribute value response with values$")
    public void theBundleResponseShouldContainAGetSpecificAttributeValueResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getResultString()));
        assertTrue("Result data is not as expected",
                response.getResultString().contains(values.get(PlatformSmartmeteringKeys.RESPONSE_PART)));
    }
}
