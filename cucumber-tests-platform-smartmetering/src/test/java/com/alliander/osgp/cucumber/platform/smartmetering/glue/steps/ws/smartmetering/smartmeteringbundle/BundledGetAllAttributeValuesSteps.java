package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetAllAttributeValuesSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get all attribute values action$")
    public void theBundleRequestContainsAGetAllAttributeValuesAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetAllAttributeValuesRequest action = new GetAllAttributeValuesRequest();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get all attribute values response$")
    public void theBundleResponseShouldContainAGetAllAttributeValuesResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);

    }

    @Then("^the bundle response should contain a get all attribute values response with values$")
    public void theBundleResponseShouldContainAGetAllAttributeValuesResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getResultString()));

    }
}
