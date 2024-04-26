// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;

public class BundledGetThdFingerprintSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get THD fingerprint action$")
  public void theBundleRequestContainsAGetThdFingerprintAction() {
    this.addActionToBundleRequest(new GetThdFingerprintRequest());
  }

  @Then("^the bundle response should contain a get THD fingerprint response with values$")
  public void theBundleResponseShouldContainAGetThdFingerprintResponseWithValues(
      final Map<String, String> expectedValues) throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof GetThdFingerprintResponse).isTrue();

    final GetThdFingerprintResponse getThdFingerprintResponse =
        (GetThdFingerprintResponse) response;

    final ThdFingerprint thdFingerprint = getThdFingerprintResponse.getThdFingerprint();

    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL1().getFingerprintValue())
        .hasSize(15);
  }
}
