//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;

public class BundledClearAlarmRegisterSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a clear alarm register action$")
  public void theBundleRequestContainsAClearAlarmRegisterAction() throws Throwable {

    final ClearAlarmRegisterRequest action = new ClearAlarmRegisterRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a clear alarm register response$")
  public void theBundleResponseShouldContainAClearAlarmRegisterResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(ActionResponse.class);
  }
}
