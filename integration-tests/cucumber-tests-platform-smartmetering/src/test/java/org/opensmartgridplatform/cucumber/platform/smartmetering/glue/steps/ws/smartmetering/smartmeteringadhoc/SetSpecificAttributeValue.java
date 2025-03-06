// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SetSpecificAttributeValueRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class SetSpecificAttributeValue {

  @Autowired
  private SmartMeteringAdHocRequestClient<
          SetSpecificAttributeValueAsyncResponse, SetSpecificAttributeValueRequest>
      requestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          SetSpecificAttributeValueResponse, SetSpecificAttributeValueAsyncRequest>
      responseClient;

  @When("^the Set Specific Attribute Value request is received$")
  public void whenTheSetSpecificAttributeValueRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetSpecificAttributeValueRequest request =
        SetSpecificAttributeValueRequestFactory.fromParameterMap(settings);
    final SetSpecificAttributeValueAsyncResponse asyncResponse =
        this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the Set Specific Attribute Value response should be returned$")
  public void thenTheValueShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {

    final SetSpecificAttributeValueAsyncRequest asyncRequest =
        SetSpecificAttributeValueRequestFactory.fromScenarioContext();
    final SetSpecificAttributeValueResponse response =
        this.responseClient.getResponse(asyncRequest);

    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(settings.get(PlatformSmartmeteringKeys.RESULT));
  }
}
