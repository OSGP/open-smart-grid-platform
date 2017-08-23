package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundledGetAssociationLnObjectListSteps extends BaseBundleSteps {

    @When("^the bundle request contains a get association ln objects action$")
    public void theBundleRequestContainsAGetAssociationLnObjectsAction() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final GetAssociationLnObjectsRequest action = new GetAssociationLnObjectsRequest();

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response should contain a get association ln objects response$")
    public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AssociationLnObjectsResponse);
    }

    @Then("^the bundle response should contain a get association ln objects response with values$")
    public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AssociationLnObjectsResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getResultString()));
    }
}
