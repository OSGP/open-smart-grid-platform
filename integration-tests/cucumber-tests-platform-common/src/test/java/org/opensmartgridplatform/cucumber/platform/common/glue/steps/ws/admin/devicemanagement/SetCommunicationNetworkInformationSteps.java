/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class SetCommunicationNetworkInformationSteps {

  @Autowired private AdminDeviceManagementClient client;

  @When("receiving a set communication network information request")
  public void receivingASetCommunicationNetworkInformationRequest(
      final Map<String, String> inputSettings) throws WebServiceSecurityException {

    final SetCommunicationNetworkInformationRequest request = createRequestFromInput(inputSettings);

    final SetCommunicationNetworkInformationResponse response =
        this.client.setCommunicationNetworkInformation(request);

    ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, response);
  }

  @Then("the set communication network information response should be returned")
  public void theSetCommunicationNetworkInformationResponseShouldBeReturned(
      final Map<String, String> inputSettings) {

    final SetCommunicationNetworkInformationResponse response =
        (SetCommunicationNetworkInformationResponse)
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getResult()).as(PlatformKeys.KEY_RESULT).isNotNull();
    assertThat(response.getResult().name())
        .as(PlatformKeys.KEY_RESULT)
        .isEqualTo(inputSettings.get(PlatformKeys.KEY_RESULT));
    assertThat(response.getIpAddress()).isEqualTo(inputSettings.get(PlatformKeys.IP_ADDRESS));
    assertThat(response.getBtsId())
        .isEqualTo(Integer.parseInt(inputSettings.get(PlatformKeys.BTS_ID)));
    assertThat(response.getCellId())
        .isEqualTo(Integer.parseInt(inputSettings.get(PlatformKeys.CELL_ID)));
  }

  private SetCommunicationNetworkInformationRequest createRequestFromInput(
      Map<String, String> inputSettings) {
    final SetCommunicationNetworkInformationRequest request =
        new SetCommunicationNetworkInformationRequest();

    request.setDeviceIdentification(inputSettings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setIpAddress(inputSettings.get(PlatformKeys.IP_ADDRESS));

    if (inputSettings.get(PlatformKeys.BTS_ID) != null) {
      request.setBtsId(Integer.parseInt(inputSettings.get(PlatformKeys.BTS_ID)));
    }

    if (inputSettings.get(PlatformKeys.CELL_ID) != null) {
      request.setCellId(Integer.parseInt(inputSettings.get(PlatformKeys.CELL_ID)));
    }

    return request;
  }

  @When("receiving a set communication network information request with an invalid ip")
  public void receivingASetCommunicationNetworkInformationRequestWithAnInvalidIp(
      final Map<String, String> inputSettings) throws WebServiceSecurityException {

    final SetCommunicationNetworkInformationRequest request = createRequestFromInput(inputSettings);

    try {
      this.client.setCommunicationNetworkInformation(request);
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
    }
  }

  @Then("the set communication network information response contains soap fault")
  public void theSetCommunicationNetworkInformationResponseContainsSoapFault(
      final Map<String, String> expectedResult) {

    assertThat(
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE)
                instanceof SoapFaultClientException)
        .isTrue();

    final SoapFaultClientException response =
        (SoapFaultClientException) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
    assertThat(response.getMessage())
        .isEqualTo(getString(expectedResult, PlatformCommonKeys.KEY_MESSAGE));
  }
}
