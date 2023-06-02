//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SpecificAttributeValueRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SpecificAttributeValue {

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      requestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      responseClient;

  @When("^the get specific attribute value request is received$")
  public void whenTheGetSpecificAttributeValueRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final GetSpecificAttributeValueRequest request =
        SpecificAttributeValueRequestFactory.fromParameterMap(settings);
    final GetSpecificAttributeValueAsyncResponse asyncResponse =
        this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^a get specific attribute value response should be returned$")
  public void thenAGetSpecificAttributeValueResponseShouldBeReturned(
      final Map<String, String> settings) throws Throwable {

    final GetSpecificAttributeValueAsyncRequest asyncRequest =
        SpecificAttributeValueRequestFactory.fromScenarioContext();
    final GetSpecificAttributeValueResponse response =
        this.responseClient.getResponse(asyncRequest);

    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(settings.get(PlatformSmartmeteringKeys.RESULT));

    final String actual = response.getAttributeValueData();
    assertThat(StringUtils.isNotBlank(actual)).as("Result contains no data.").isTrue();

    final String expected = settings.get(PlatformSmartmeteringKeys.RESPONSE_PART);
    assertThat(actual.contains(expected))
        .as(
            "Result data is not as expected; expected '"
                + expected
                + "' to be part of '"
                + actual
                + "'")
        .isTrue();
  }
}
