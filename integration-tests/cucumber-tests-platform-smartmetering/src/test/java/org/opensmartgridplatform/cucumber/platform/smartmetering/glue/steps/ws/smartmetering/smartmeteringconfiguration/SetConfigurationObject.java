//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetConfigurationObjectRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetConfigurationObject {
  protected static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObject.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the set configuration object request is received$")
  public void theSetConfigurationObjectRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetConfigurationObjectRequest setConfigurationObjectRequest =
        SetConfigurationObjectRequestFactory.fromParameterMap(requestData);

    final SetConfigurationObjectAsyncResponse setConfigurationObjectAsyncResponse =
        this.smartMeteringConfigurationClient.setConfigurationObject(setConfigurationObjectRequest);

    LOGGER.info(
        "Set configuration object response is received {}", setConfigurationObjectAsyncResponse);

    assertThat(setConfigurationObjectAsyncResponse)
        .as("Set configuration object response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setConfigurationObjectAsyncResponse.getCorrelationUid());
  }

  @Then("^the configuration object should be set on the device$")
  public void theConfigurationObjectShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest =
        SetConfigurationObjectRequestFactory.fromScenarioContext();
    final SetConfigurationObjectResponse setConfigurationObjectResponse =
        this.smartMeteringConfigurationClient.retrieveSetConfigurationObjectResponse(
            setConfigurationObjectAsyncRequest);

    assertThat(setConfigurationObjectResponse.getResult())
        .as("Set configuration object result")
        .isEqualTo(OsgpResultType.OK);
  }
}
