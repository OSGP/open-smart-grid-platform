// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ClearAlarmRegisterRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ClearAlarmRegisterSteps {

  @Autowired
  private SmartMeteringMonitoringRequestClient<
          ClearAlarmRegisterAsyncResponse, ClearAlarmRegisterRequest>
      smMonitoringRequestClientClearAlarmRegister;

  @Autowired
  private SmartMeteringMonitoringResponseClient<
          ClearAlarmRegisterResponse, ClearAlarmRegisterAsyncRequest>
      smMonitoringResponseClientClearAlarmRegister;

  @When("^the Clear Alarm Code request is received$")
  public void theClearAlarmCodeRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final ClearAlarmRegisterRequest clearAlarmRegisterRequest =
        ClearAlarmRegisterRequestFactory.fromParameterMap(settings);

    final ClearAlarmRegisterAsyncResponse clearAlarmRegisterAsyncResponse =
        this.smMonitoringRequestClientClearAlarmRegister.doRequest(clearAlarmRegisterRequest);

    assertThat(clearAlarmRegisterAsyncResponse)
        .as("ClearAlarmRegisterAsyncResponse should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, clearAlarmRegisterAsyncResponse.getCorrelationUid());
  }

  @Then("^the Clear Alarm Code response should be returned$")
  public void theClearAlarmCodeResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final ClearAlarmRegisterAsyncRequest clearAlarmRegisterAsyncRequest =
        ClearAlarmRegisterRequestFactory.fromScenarioContext();

    final ClearAlarmRegisterResponse clearAlarmRegisterResponse =
        this.smMonitoringResponseClientClearAlarmRegister.getResponse(
            clearAlarmRegisterAsyncRequest);

    assertThat(clearAlarmRegisterResponse)
        .as("ClearAlarmRegisterResponse should not be null")
        .isNotNull();
    assertThat(clearAlarmRegisterResponse.getResult())
        .as("Expected OsgpResultType should not be null")
        .isNotNull();
    assertThat(clearAlarmRegisterResponse.getResult().name())
        .as("Result is not 'OK' as expected.")
        .isEqualTo(settings.get(PlatformSmartmeteringKeys.RESULT));
  }
}
