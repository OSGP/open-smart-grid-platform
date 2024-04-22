// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.GetThdFingerprintRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ThdFingerprintSteps {

  @Autowired
  private SmartMeteringMonitoringRequestClient<
          GetThdFingerprintAsyncResponse, GetThdFingerprintRequest>
      requestClient;

  @Autowired
  private SmartMeteringMonitoringResponseClient<
          GetThdFingerprintResponse, GetThdFingerprintAsyncRequest>
      responseClient;

  @When("^the get THD fingerprint request is received$")
  public void theGetThdFingerprintRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final GetThdFingerprintRequest request =
        GetThdFingerprintRequestFactory.fromParameterMap(settings);

    final GetThdFingerprintAsyncResponse asyncResponse = this.requestClient.doRequest(request);
    assertThat(asyncResponse).isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the THD fingerprint result should be returned$")
  public void theThdFingerprintResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final GetThdFingerprintAsyncRequest asyncRequest =
        GetThdFingerprintRequestFactory.fromScenarioContext();
    final GetThdFingerprintResponse response = this.responseClient.getResponse(asyncRequest);

    assertThat(response).as("GetThdFingerprintResponse should not be null").isNotNull();
    assertThat(response.getThdFingerprint()).as("Expected THD fingerprint").isNotNull();
  }
}
