//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.AllAttributeValuesRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class AllAttributeValues {

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetAllAttributeValuesAsyncResponse, GetAllAttributeValuesRequest>
      requestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetAllAttributeValuesResponse, GetAllAttributeValuesAsyncRequest>
      responseClient;

  @When("^the get all attribute values request is received$")
  public void whenTheGetAllAttributeValuesRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final GetAllAttributeValuesRequest request =
        AllAttributeValuesRequestFactory.fromParameterMap(settings);
    final GetAllAttributeValuesAsyncResponse asyncResponse = this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^a get all attribute values response should be returned$")
  public void thenAGetAllAttributeValuesResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final GetAllAttributeValuesAsyncRequest asyncRequest =
        AllAttributeValuesRequestFactory.fromScenarioContext();
    final GetAllAttributeValuesResponse response = this.responseClient.getResponse(asyncRequest);

    assertThat(response.getResult())
        .as("Result is not as expected")
        .isEqualTo(OsgpResultType.fromValue(settings.get(PlatformKeys.KEY_RESULT)));

    assertThat(StringUtils.isNotBlank(response.getOutput()))
        .as("Response should contain Output")
        .isTrue();
  }
}
