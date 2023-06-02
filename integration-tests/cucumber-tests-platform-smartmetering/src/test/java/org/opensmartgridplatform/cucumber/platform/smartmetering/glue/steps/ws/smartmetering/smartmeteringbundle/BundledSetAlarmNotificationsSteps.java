//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetAlarmNotificationsRequestBuilder;

public class BundledSetAlarmNotificationsSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a set alarm notifications action$")
  public void theBundleRequestContainsASetAlarmNotificationsAction() throws Throwable {

    final SetAlarmNotificationsRequest action =
        new SetAlarmNotificationsRequestBuilder().withDefaults().build();

    this.addActionToBundleRequest(action);
  }

  @Given("^the bundle request contains a set alarm notifications action with parameters$")
  public void theBundleRequestContainsASetAlarmNotificationsAction(
      final Map<String, String> parameters) throws Throwable {

    final SetAlarmNotificationsRequest action =
        new SetAlarmNotificationsRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a set alarm notifications response$")
  public void theBundleResponseShouldContainASetAlarmNotificationsResponse() throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a set alarm notifications response with values$")
  public void theBundleResponseShouldContainASetAlarmNotificationsResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
  }
}
