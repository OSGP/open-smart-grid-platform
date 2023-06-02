//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetPushSetupAlarmRequestBuilder;

public class BundledSetPushSetupAlarmSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a set push setup alarm action$")
  public void theBundleRequestContainsASetPushSetupAlarmAction() throws Throwable {

    final SetPushSetupAlarmRequest action =
        new SetPushSetupAlarmRequestBuilder().withDefaults().build();

    this.addActionToBundleRequest(action);
  }

  @Given("^the bundle request contains a set push setup alarm action with parameters$")
  public void theBundleRequestContainsASetPushSetupAlarmAction(final Map<String, String> parameters)
      throws Throwable {

    final SetPushSetupAlarmRequest action =
        new SetPushSetupAlarmRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a set push setup alarm response$")
  public void theBundleResponseShouldContainASetPushSetupAlarmResponse() throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a set push setup alarm response with values$")
  public void theBundleResponseShouldContainASetPushSetupAlarmResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
  }
}
