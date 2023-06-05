// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class BundledSynchronizeTimeSteps extends BaseBundleSteps {

  private static final String DEFAULT_TIMEZONE = "Europe/Amsterdam";

  @Given("^the bundle request contains a synchronize time action$")
  public void theBundleRequestContainsASynchronizeTimeAction() throws Throwable {

    this.theBundleRequestContainsAValidSynchronizeTimeAction(DEFAULT_TIMEZONE);
  }

  @Given("^the bundle request contains a valid synchronize time action for timezone \"([^\"]*)\"")
  public void theBundleRequestContainsAValidSynchronizeTimeAction(final String timeZoneId) {
    final SynchronizeTimeRequest action = new SynchronizeTimeRequest();
    action.setTimeZone(timeZoneId);
    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a synchronize time response$")
  public void theBundleResponseShouldContainASynchronizeTimeResponse() throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a synchronize time response with values$")
  public void theBundleResponseShouldContainASynchronizeTimeResponse(
      final Map<String, String> values) throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response.getResult())
        .isEqualTo(OsgpResultType.fromValue(values.get(PlatformSmartmeteringKeys.RESULT)));
  }
}
