// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetSpecificAttributeValueRequestBuilder;

public class BundledGetSpecificAttributeValueSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get specific attribute value action$")
  public void theBundleRequestContainsAGetSpecificAttributeValueAction() throws Throwable {

    final GetSpecificAttributeValueRequest action =
        new GetSpecificAttributeValueRequestBuilder().withDefaults().build();

    this.addActionToBundleRequest(action);
  }

  @Given("^the bundle request contains a get specific attribute value action with parameters$")
  public void theBundleRequestContainsAGetSpecificAttributeValueAction(
      final Map<String, String> parameters) throws Throwable {

    final GetSpecificAttributeValueRequest action =
        new GetSpecificAttributeValueRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get specific attribute value response$")
  public void theBundleResponseShouldContainAGetSpecificAttributeValueResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a get specific attribute value response with values$")
  public void theBundleResponseShouldContainAGetSpecificAttributeValueResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    assertThat(StringUtils.isNotBlank(response.getResultString()))
        .as("Result contains no data.")
        .isTrue();
    assertThat(
            response
                .getResultString()
                .contains(values.get(PlatformSmartmeteringKeys.RESPONSE_PART)))
        .as("Result data is not as expected")
        .isTrue();
  }
}
