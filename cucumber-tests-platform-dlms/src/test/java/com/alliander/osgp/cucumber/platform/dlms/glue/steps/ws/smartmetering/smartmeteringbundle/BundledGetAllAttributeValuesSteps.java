package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledGetAllAttributeValuesSteps extends BundleStepsBase {

    @When("^a get all attribute values action is part of the bundle request$")
    public void aGetAllAttributeValuesActionIsPartOfTheBundleRequest() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(SCENARIO_CONTEXT_BUNDLE_REQUEST);

        final GetAllAttributeValuesRequest action = new GetAllAttributeValuesRequest();

        request.getActions().getActionList().add(action);

        this.increaseCount(SCENARIO_CONTEXT_BUNDLE_ACTIONS);
    }

    @Then("^the bundle response should contain a get all attribute values response$")
    public void allAttributeValuesShouldBePartOfTheBundleResponse(final Map<String, String> settings) throws Throwable {

        this.ensureBundleResponse(settings);

        final BundleResponse response = (BundleResponse) ScenarioContext.Current()
                .get(SCENARIO_CONTEXT_BUNDLE_RESPONSE);

        final ActionResponse actionResponse = (ActionResponse) response.getAllResponses().getResponseList()
                .get(this.getAndIncreaseCount(SCENARIO_CONTEXT_BUNDLE_RESPONSES));

        assertEquals("Result is not as expected.", settings.get(Keys.RESULT), actionResponse.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(actionResponse.getResultString()));

    }
}
