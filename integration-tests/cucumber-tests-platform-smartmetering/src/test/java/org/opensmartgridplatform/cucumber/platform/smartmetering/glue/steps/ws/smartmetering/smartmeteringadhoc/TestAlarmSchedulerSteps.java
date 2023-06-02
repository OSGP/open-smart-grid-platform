//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.TestAlarmSchedulerRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TestAlarmSchedulerSteps {

  @Autowired
  private SmartMeteringAdHocRequestClient<
          TestAlarmSchedulerAsyncResponse, TestAlarmSchedulerRequest>
      requestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          TestAlarmSchedulerResponse, TestAlarmSchedulerAsyncRequest>
      responseClient;

  @When("^receiving a test alarm scheduler request$")
  public void receivingATestAlarmSchedulerRequest(final Map<String, String> settings)
      throws Throwable {

    final TestAlarmSchedulerRequest request =
        TestAlarmSchedulerRequestFactory.fromParameterMap(settings);
    final TestAlarmSchedulerAsyncResponse asyncResponse = this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^a response is received$")
  public void aResponseIsReceived(final Map<String, String> settings) throws Throwable {

    final TestAlarmSchedulerAsyncRequest asyncRequest =
        TestAlarmSchedulerRequestFactory.fromScenarioContext();
    final TestAlarmSchedulerResponse response = this.responseClient.getResponse(asyncRequest);
    assertThat(response.getResult()).as("Results was not successful").isEqualTo(OsgpResultType.OK);
  }
}
