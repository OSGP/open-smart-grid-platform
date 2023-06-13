// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.GetGsmDiagnosticRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.GetGsmDiagnosticResponseFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class GetGsmDiagnostic {

  @Autowired
  private SmartMeteringManagementRequestClient<
          GetGsmDiagnosticAsyncResponse, GetGsmDiagnosticRequest>
      smManagementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          GetGsmDiagnosticResponse, GetGsmDiagnosticAsyncRequest>
      smManagementResponseClient;

  private static final String OPERATION = "Get gsm diagnostic";

  @When("^a get gsm diagnostic request is received$")
  public void aGetGsmDiagnosticRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final GetGsmDiagnosticRequest request =
        GetGsmDiagnosticRequestFactory.fromParameterMap(settings);

    final GetGsmDiagnosticAsyncResponse asyncResponse =
        this.smManagementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("getGsmDiagnosticAsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the get gsm diagnostic response is returned with values$")
  public void theGetGsmDiagnosticResponseIsReturned(final Map<String, String> expectedValues)
      throws Throwable {

    final GetGsmDiagnosticAsyncRequest asyncRequest =
        GetGsmDiagnosticRequestFactory.fromScenarioContext();
    final GetGsmDiagnosticResponse response =
        this.smManagementResponseClient.getResponse(asyncRequest);

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticResponse
        expectedResponse = GetGsmDiagnosticResponseFactory.fromParameterMap(expectedValues);

    assertThat(response.getGetGsmDiagnosticResponseData())
        .usingRecursiveComparison()
        .ignoringFields("captureTime") // Reading of captureTime is disabled for now
        .isEqualTo(expectedResponse);
  }

  @Then("^get gsm diagnostic request should return an exception$")
  public void GetGsmDiagnosticRequestShouldReturnAnException() throws Throwable {

    final GetGsmDiagnosticAsyncRequest asyncRequest =
        GetGsmDiagnosticRequestFactory.fromScenarioContext();
    try {
      this.smManagementResponseClient.getResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown.");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }
}
