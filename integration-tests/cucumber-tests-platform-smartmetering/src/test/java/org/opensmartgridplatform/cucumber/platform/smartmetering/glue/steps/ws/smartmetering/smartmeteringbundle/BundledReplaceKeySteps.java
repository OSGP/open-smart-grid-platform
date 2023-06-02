//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetKeysRequestDataFactory;

public class BundledReplaceKeySteps extends BaseBundleSteps {

  @Given("^the bundle request contains a replace keys action$")
  public void theBundleRequestContainsASetClockConfigurationAction(
      final Map<String, String> settings) throws Throwable {

    final SetKeysRequest action =
        this.mapperFacade.map(
            SetKeysRequestDataFactory.fromParameterMap(settings), SetKeysRequest.class);
    this.addActionToBundleRequest(action);
  }

  @Given("the bundle request contains a generate and replace keys action")
  public void theBundleRequestContainsAGenerateAndReplaceKeysAction() {
    final GenerateAndReplaceKeysRequest action = new GenerateAndReplaceKeysRequest();
    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a replace keys response with values$")
  public void theBundleResponseShouldContainAReplaceKeysResponse(final Map<String, String> values)
      throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    assertThat(response.getResultString())
        .as("ResultString is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT_STRING));
  }

  @Then("the bundle response should contain a generate replace keys response with values")
  public void theBundleResponseShouldContainAGenerateReplaceKeysResponseWithValues(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    assertThat(response.getResultString())
        .as("ResultString is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT_STRING));
  }
}
