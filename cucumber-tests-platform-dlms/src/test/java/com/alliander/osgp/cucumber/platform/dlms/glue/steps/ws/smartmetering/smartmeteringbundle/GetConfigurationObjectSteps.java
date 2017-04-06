package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class GetConfigurationObjectSteps extends AbstractBundleSteps {

    @Given("^a get configuration object is part of a bundled request$")
    public void aGetConfigurationObjectIsPartOfBundledRequest() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current()
                .get(Keys.SCENARIO_CONTEXT_BUNDLE_REQUEST);

        request.getActions().getActionList().add(new GetConfigurationObjectRequest());
        this.increaseCount(Keys.SCENARIO_CONTEXT_BUNDLE_ACTIONS);
    }

    @Then("^the bundle response contains a get configuration object response$")
    public void theBundleResponseContainsConfigurationObjectResponse(final Map<String, String> settings)
            throws Throwable {

        this.ensureBundleResponse(settings);

        final BundleResponse response = (BundleResponse) ScenarioContext.Current()
                .get(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSE);

        final Response actionResponse = response.getAllResponses().getResponseList()
                .get(this.getAndIncreaseCount(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSES));

        Assert.assertTrue("response should be a GetConfigurationResponse object",
                actionResponse instanceof GetConfigurationResponse);
        final String responseString = ((GetConfigurationResponse) actionResponse).getConfigurationData();

        settings.keySet().forEach(key -> this.assertResponseString(responseString, key, settings));
    }

    private void assertResponseString(final String responseString, final String key,
            final Map<String, String> settings) {
        final String testString = StringUtils.replace(responseString, " ", "");
        final String expectValue = settings.get(key);
        Assert.assertTrue("reponse should contain " + key, testString.indexOf(key) >= 0);
        Assert.assertTrue(key + " should be " + expectValue, testString.indexOf("=" + expectValue) >= 0);
    }

}
