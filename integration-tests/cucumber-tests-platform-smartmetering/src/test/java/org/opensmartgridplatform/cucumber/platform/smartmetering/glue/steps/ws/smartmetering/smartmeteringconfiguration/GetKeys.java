/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetKeysRequestFactory.getSecretTypesFromParameterMap;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SecretType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

public class GetKeys {

  private static final String OPERATION = "Get keys";

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^a get keys request is received$")
  public void aGetKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {

    final GetKeysRequest request = GetKeysRequestFactory.fromParameterMap(settings);

    final GetKeysAsyncResponse asyncResponse =
        this.smartMeteringConfigurationClient.getKeys(request);

    assertThat(asyncResponse).as("getKeysAsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the get keys response should return the requested keys$")
  public void theGetKeysResponseIsReturned(final Map<String, String> expectedValues)
      throws Throwable {

    final GetKeysAsyncRequest asyncRequest = GetKeysRequestFactory.fromScenarioContext();
    final GetKeysResponse response =
        this.smartMeteringConfigurationClient.retrieveGetKeysResponse(asyncRequest);

    assertThat(response).isNotNull();

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);

    final byte[] key1 = response.getGetKeysResponseData().get(0).getSecretValue();
    final SecretType key1Type = response.getGetKeysResponseData().get(0).getSecretType();
    final byte[] key2 = response.getGetKeysResponseData().get(1).getSecretValue();
    final SecretType key2Type = response.getGetKeysResponseData().get(1).getSecretType();

    final List<SecretType> secretTypes = getSecretTypesFromParameterMap(expectedValues);

    assertThat(key1Type).isEqualTo(secretTypes.get(0));
    assertThat(key1).isNotEmpty();
    assertThat(key2Type).isEqualTo(secretTypes.get(1));
    assertThat(key2).isNotEmpty();
  }
}
