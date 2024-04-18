// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetThdConfigurationRequestBuilder;

public class BundledSetThdConfigurationSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a set THD configuration action with parameters$")
  public void theBundleRequestContainsASetThdConfigurationAction(
      final Map<String, String> parameters) throws Throwable {

    final SetThdConfigurationRequest action =
        new SetThdConfigurationRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a set THD configuration response$")
  public void theBundleResponseShouldContainASetThdConfigurationResponse() throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response).as("Not a valid response").isInstanceOf(ActionResponse.class);
  }

  @Then("^the bundle response should contain a set THD configuration response with values$")
  public void theBundleResponseShouldContainASetThdConfigurationResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).as("Not a valid response").isInstanceOf(ActionResponse.class);
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
  }
}
