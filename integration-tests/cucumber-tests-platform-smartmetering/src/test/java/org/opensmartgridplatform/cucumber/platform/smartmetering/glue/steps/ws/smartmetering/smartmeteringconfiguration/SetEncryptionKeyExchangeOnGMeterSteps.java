/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetMbusUserKeyByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetEncryptionKeyExchangeOnGMeterSteps {
  protected static final Logger LOGGER =
      LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterSteps.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the exchange user key request is received$")
  public void theExchangeUserKeyRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest =
        SetEncryptionKeyExchangeOnGMeterRequestFactory.fromParameterMap(requestData);

    final SetEncryptionKeyExchangeOnGMeterAsyncResponse
        setEncryptionKeyExchangeOnGMeterAsyncResponse =
            this.smartMeteringConfigurationClient.setEncryptionKeyExchangeOnGMeter(
                setEncryptionKeyExchangeOnGMeterRequest);

    assertThat(setEncryptionKeyExchangeOnGMeterAsyncResponse)
        .as("Set encryptionKey exchange on GMeter async response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setEncryptionKeyExchangeOnGMeterAsyncResponse.getCorrelationUid());
  }

  @Then("^the exchange user key response should be returned$")
  public void theExchangeUserKeyResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final SetEncryptionKeyExchangeOnGMeterAsyncRequest
        setEncryptionKeyExchangeOnGMeterAsyncRequest =
            SetEncryptionKeyExchangeOnGMeterRequestFactory.fromScenarioContext();
    final SetEncryptionKeyExchangeOnGMeterResponse setEncryptionKeyExchangeOnGMeterResponse =
        this.smartMeteringConfigurationClient.retrieveSetEncryptionKeyExchangeOnGMeterResponse(
            setEncryptionKeyExchangeOnGMeterAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
    assertThat(setEncryptionKeyExchangeOnGMeterResponse.getResult())
        .as("Set Encryption Key Exchange On G-Meter result must not be null")
        .isNotNull();
    assertThat(setEncryptionKeyExchangeOnGMeterResponse.getResult().name())
        .as("Set Encryption Key Exchange On G-Meter result")
        .isEqualTo(expectedResult);
  }

  @When("^the set m-bus user key by channel request is received$")
  public void theSetMbusUserKeyByChannelRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest =
        SetMbusUserKeyByChannelRequestFactory.fromParameterMap(requestData);
    final SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannelAsyncResponse =
        this.smartMeteringConfigurationClient.setMbusUserKeyByChannel(
            setMbusUserKeyByChannelRequest);

    assertThat(setMbusUserKeyByChannelAsyncResponse)
        .as("Set M-Bus User Key By Channel async response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setMbusUserKeyByChannelAsyncResponse.getCorrelationUid());
  }

  @Then("^the set m-bus user key by channel response should be returned$")
  public void theSetMbusUserKeyByChannelResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest =
        SetMbusUserKeyByChannelRequestFactory.fromScenarioContext();
    final SetMbusUserKeyByChannelResponse setMbusUserKeyByChannelResponse =
        this.smartMeteringConfigurationClient.getSetMbusUserKeyByChannelResponse(
            setMbusUserKeyByChannelAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
    assertThat(setMbusUserKeyByChannelResponse.getResult())
        .as("Set M-Bus User Key By Channel result must not be null")
        .isNotNull();
    assertThat(setMbusUserKeyByChannelResponse.getResult().name())
        .as("Set M-Bus User Key By Channel result")
        .isEqualTo(expectedResult);
  }
}
