// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsGasResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FaultResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;

public class BundledGetActualMeterReadsSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get actual meter reads action$")
  public void theBundleRequestContainsAGetActualMeterReadsAction() throws Throwable {

    final GetActualMeterReadsRequest action = new GetActualMeterReadsRequest();

    this.addActionToBundleRequest(action);
  }

  @Given("^the bundle request contains a get actual meter reads gas action$")
  public void theBundleRequestContainsAGetActualMeterReadsGasAction(
      final Map<String, String> settings) {

    final GetActualMeterReadsGasRequest action = new GetActualMeterReadsGasRequest();
    action.setDeviceIdentification(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEFAULT_SMART_METER_GAS_DEVICE_IDENTIFICATION));

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get actual meter reads response$")
  public void theBundleResponseShouldContainAGetActualMeterReadsResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActualMeterReadsResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a get actual meter reads gas response$")
  public void theBundleResponseShouldContainAGetActualMeterReadsGasResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActualMeterReadsGasResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a fault response$")
  public void theBundleResponseShouldContainAValidationErrorResponse(
      final Map<String, String> settings) throws Throwable {

    final Response response = this.getNextBundleResponse();
    assertThat(response).isInstanceOf(FaultResponse.class);
    final FaultResponse faultResponse = (FaultResponse) response;

    if (settings.containsKey(PlatformKeys.MESSAGE)) {
      final String expected = settings.get(PlatformKeys.MESSAGE);
      assertThat(faultResponse.getMessage()).isEqualTo(expected);
    }
    if (settings.containsKey(PlatformKeys.INNER_MESSAGE)) {
      final String expected = settings.get(PlatformKeys.INNER_MESSAGE);
      assertThat(faultResponse.getInnerMessage()).isEqualTo(expected);
    }
  }
}
