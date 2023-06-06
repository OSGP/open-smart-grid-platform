// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;

public class BundledGetAdministrativeStatusSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get administrative status action$")
  public void theBundleRequestContainsAGetAdministrativeStatusAction() throws Throwable {

    final GetAdministrativeStatusRequest action = new GetAdministrativeStatusRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get administrative status response$")
  public void theBundleResponseShouldContainAGetAdministrativeStatusResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof AdministrativeStatusResponse)
        .as("Not a valid response")
        .isTrue();
  }
}
