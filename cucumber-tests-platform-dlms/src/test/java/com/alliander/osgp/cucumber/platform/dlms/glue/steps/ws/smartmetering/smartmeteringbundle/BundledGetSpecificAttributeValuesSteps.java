package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.adhoc.SpecificAttributeValueRequestBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledGetSpecificAttributeValuesSteps extends BundleStepsBase {

    @When("^A get specific attribute value action is part of the bundle request$")
    public void aGetSpecificAttributeValueActionIsPartOfTheBundleRequest(final Map<String, String> settings)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(SCENARIO_CONTEXT_BUNDLE_REQUEST);

        final SpecificAttributeValueRequest action = new SpecificAttributeValueRequestBuilder()
                .fromParameterMap(settings).build();

        request.getActions().getActionList().add(action);

        this.increaseCount(SCENARIO_CONTEXT_BUNDLE_ACTIONS);
    }

    @Then("^A specific attribute value should be part of the bundle response$")
    public void aSpecificAttributeValueShouldBePartOfTheBundleResponse(final Map<String, String> settings)
            throws Throwable {

        this.ensureBundleResponse(settings);

        final BundleResponse response = (BundleResponse) ScenarioContext.Current()
                .get(SCENARIO_CONTEXT_BUNDLE_RESPONSE);

        final SpecificAttributeValueResponse actionResponse = (SpecificAttributeValueResponse) response
                .getAllResponses().getResponseList().get(this.getAndIncreaseCount(SCENARIO_CONTEXT_BUNDLE_RESPONSES));

        assertTrue(actionResponse.getConfigurationData().contains(settings.get(Keys.RESPONSE_PART)));

    }
}
