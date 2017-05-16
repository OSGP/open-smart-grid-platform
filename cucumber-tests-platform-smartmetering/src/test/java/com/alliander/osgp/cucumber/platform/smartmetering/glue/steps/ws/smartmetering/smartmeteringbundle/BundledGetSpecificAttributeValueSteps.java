package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetSpecificAttributeValueRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledGetSpecificAttributeValueSteps extends BaseBundleSteps {

    @When("^a get specific attribute value action is part of the bundle request$")
    public void aGetSpecificAttributeValueActionIsPartOfTheBundleRequest(final Map<String, String> settings)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetSpecificAttributeValueRequest action = new GetSpecificAttributeValueRequestBuilder()
                .fromParameterMap(settings).build();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get specific attribute value response$")
    public void aSpecificAttributeValueShouldBePartOfTheBundleResponse(final Map<String, String> settings)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertEquals("Result is not as expected.", settings.get(PlatformSmartmeteringKeys.RESULT), response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getResultString()));
        assertTrue("Result data is not as expected",
                response.getResultString().contains(settings.get(PlatformSmartmeteringKeys.RESPONSE_PART)));
    }
}
