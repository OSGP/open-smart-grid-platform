//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.GetGsmDiagnosticResponseFactory;

public class BundledGetGsmDiagnosticSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get gsm diagnostic action$")
  public void theBundleRequestContainsAGetGsmDiagnosticAction() throws Throwable {

    final GetGsmDiagnosticRequest action = new GetGsmDiagnosticRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get gsm diagnostic response with values$")
  public void theBundleResponseShouldContainAGetGsmDiagnosticResponse(
      final Map<String, String> expectedValues) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(GetGsmDiagnosticResponse.class);

    final GetGsmDiagnosticResponse getGsmDiagnosticResponse = (GetGsmDiagnosticResponse) response;

    final GetGsmDiagnosticResponse expectedResponse =
        GetGsmDiagnosticResponseFactory.fromParameterMap(expectedValues);

    assertThat(getGsmDiagnosticResponse)
        .usingRecursiveComparison()
        .ignoringFields("captureTime") // Reading of captureTime is disabled for now
        .isEqualTo(expectedResponse);
  }
}
