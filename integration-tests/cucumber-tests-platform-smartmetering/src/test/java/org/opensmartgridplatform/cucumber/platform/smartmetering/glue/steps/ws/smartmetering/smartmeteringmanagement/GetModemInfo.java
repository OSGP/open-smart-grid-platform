/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.GetModemInfoRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.GetModemInfoResponseValidator;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class GetModemInfo {

  @Autowired
  private SmartMeteringManagementRequestClient<GetModemInfoAsyncResponse, GetModemInfoRequest>
      smManagementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<GetModemInfoResponse, GetModemInfoAsyncRequest>
      smManagementResponseClient;

  private static final String OPERATION = "Get modem info";

  @When("^a get modem info request is received$")
  public void aGetModemInfoRequestIsReceived(final Map<String, String> settings) throws Throwable {

    final GetModemInfoRequest request = GetModemInfoRequestFactory.fromParameterMap(settings);

    final GetModemInfoAsyncResponse asyncResponse =
        this.smManagementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("getModemInfoAsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the get modem info response is returned with values$")
  public void theGetModemInfoResponseIsReturned(final Map<String, String> expectedValues)
      throws Throwable {

    final GetModemInfoAsyncRequest asyncRequest = GetModemInfoRequestFactory.fromScenarioContext();
    final GetModemInfoResponse response = this.smManagementResponseClient.getResponse(asyncRequest);

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);

    // Add asserts on settings
    GetModemInfoResponseValidator.validate(response.getGetModemInfoResponseData(), expectedValues);
  }

  @Then("^get modem info request should return an exception$")
  public void GetModemInfoRequestShouldReturnAnException() throws Throwable {

    final GetModemInfoAsyncRequest asyncRequest = GetModemInfoRequestFactory.fromScenarioContext();
    try {
      this.smManagementResponseClient.getResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown.");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }
}
