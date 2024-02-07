// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.EncryptionKeyStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class GetMbusEncryptionKeyStatusByChannelSteps {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetMbusEncryptionKeyStatusSteps.class);
  private static final String OPERATION = "Get M-Bus encryption key status by channel";

  @Autowired private SmartMeteringConfigurationClient smartMeterConfigurationClient;

  @When("^a get M-Bus encryption key status by channel request is received$")
  public void aGetMBusEncryptionKeyStatusByChannelRequestIsReceived(
      final Map<String, String> settings) throws Throwable {

    final GetMbusEncryptionKeyStatusByChannelRequest request =
        GetMbusEncryptionKeyStatusByChannelRequestFactory.fromParameterMap(settings);
    final GetMbusEncryptionKeyStatusByChannelAsyncResponse asyncResponse =
        this.smartMeterConfigurationClient.getMbusEncryptionKeyStatusByChannel(request);

    assertThat(asyncResponse).as(OPERATION + ": Async response should not be null").isNotNull();
    LOGGER.info(OPERATION + ": Async response is received {}", asyncResponse);

    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
            asyncResponse.getDeviceIdentification());
  }

  @Then("^the get M-Bus encryption key status by channel response is returned$")
  public void theGetMBusEncryptionKeyStatusByChannelResponseIsReturned(
      final Map<String, String> settings) throws Throwable {

    final GetMbusEncryptionKeyStatusByChannelAsyncRequest asyncRequest =
        GetMbusEncryptionKeyStatusByChannelRequestFactory.fromScenarioContext();
    final GetMbusEncryptionKeyStatusByChannelResponse response =
        this.smartMeterConfigurationClient.retrieveGetMbusEncryptionKeyStatusByChannelResponse(
            asyncRequest);
    final EncryptionKeyStatus expectedEncryptionKeyStatus = EncryptionKeyStatus.valueOf(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTION_KEY_STATUS));

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);
    assertThat(response.getEncryptionKeyStatus())
        .as(OPERATION + ", Checking EncryptionKeyStatus:")
        .isEqualTo(expectedEncryptionKeyStatus);
  }

  @Then("^the get M-Bus encryption key status by channel request should return an exception$")
  public void theGetMbusEncryptionKeyStatusByChannelRequestShouldReturnAnException()
      throws WebServiceSecurityException, GeneralSecurityException, IOException {

    final GetMbusEncryptionKeyStatusByChannelAsyncRequest asyncRequest =
        GetMbusEncryptionKeyStatusByChannelRequestFactory.fromScenarioContext();
    try {
      this.smartMeterConfigurationClient.retrieveGetMbusEncryptionKeyStatusByChannelResponse(
          asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown.");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }
}
