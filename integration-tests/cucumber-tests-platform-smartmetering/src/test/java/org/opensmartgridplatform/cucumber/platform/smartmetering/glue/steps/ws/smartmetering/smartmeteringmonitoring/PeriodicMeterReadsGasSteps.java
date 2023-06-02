//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement.AbstractFindEventsReads;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.PeriodicMeterReadsGasRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PeriodicMeterReadsGasSteps {
  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindEventsReads.class);

  @Autowired
  private SmartMeteringMonitoringRequestClient<
          PeriodicMeterReadsGasAsyncResponse, PeriodicMeterReadsGasRequest>
      requestClient;

  @Autowired
  private SmartMeteringMonitoringResponseClient<
          PeriodicMeterReadsGasResponse, PeriodicMeterReadsGasAsyncRequest>
      responseClient;

  @When("^the get \"([^\"]*)\" meter reads gas request is received$")
  public void theGetMeterReadsGasRequestIsReceived(
      final String periodType, final Map<String, String> settings) throws Throwable {

    final PeriodicMeterReadsGasRequest request =
        PeriodicMeterReadsGasRequestFactory.fromParameterMap(settings);

    final PeriodicMeterReadsGasAsyncResponse asyncResponse = this.requestClient.doRequest(request);
    assertThat(asyncResponse).isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the \"([^\"]*)\" meter reads gas result should be returned$")
  public void theMeterReadsGasResultShouldBeReturned(
      final String periodType, final Map<String, String> settings) throws Throwable {

    final PeriodicMeterReadsGasAsyncRequest asyncRequest =
        PeriodicMeterReadsGasRequestFactory.fromScenarioContext();

    LOGGER.warn("Asyncrequest: {} ", asyncRequest);

    final PeriodicMeterReadsGasResponse response = this.responseClient.getResponse(asyncRequest);

    assertThat(response).as("PeriodicMeterReadsGasResponse should not be null").isNotNull();
    assertThat(response.getPeriodType())
        .as("PeriodType should match")
        .isEqualTo(PeriodType.fromValue(periodType));
    assertThat(response.getPeriodicMeterReadsGas())
        .as("Expected periodic meter reads gas")
        .isNotNull();
  }
}
