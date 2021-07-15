/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.DecoupleMbusDeviceRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class DecoupleDeviceSteps extends AbstractSmartMeteringSteps {

  @Autowired private SmartMeteringInstallationClient smartMeteringInstallationClient;

  @When(
      "^the Decouple G-meter \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received for an unknown gateway$")
  public void theDecoupleGMeterFromEMeterRequestIsReceivedForAnUnknownDevice(
      final String gasMeter, final String eMeter) throws WebServiceSecurityException {

    final DecoupleMbusDeviceRequest request =
        DecoupleMbusDeviceRequestFactory.forGatewayAndMbusDevice(eMeter, gasMeter);

    try {
      this.smartMeteringInstallationClient.decoupleMbusDevice(request);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @When(
      "^the Decouple G-meter \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received for an inactive gateway$")
  public void theDecoupleGMeterFromEMeterRequestIsReceivedForAnInactiveDevice(
      final String gasMeter, final String eMeter) throws WebServiceSecurityException {

    final DecoupleMbusDeviceRequest request =
        DecoupleMbusDeviceRequestFactory.forGatewayAndMbusDevice(eMeter, gasMeter);

    try {
      this.smartMeteringInstallationClient.decoupleMbusDevice(request);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @When("^the Decouple G-meter \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received$")
  public void theDecoupleGMeterRequestIsReceived(final String gasMeter, final String eMeter)
      throws WebServiceSecurityException {

    final DecoupleMbusDeviceRequest request =
        DecoupleMbusDeviceRequestFactory.forGatewayAndMbusDevice(eMeter, gasMeter);
    final DecoupleMbusDeviceAsyncResponse asyncResponse =
        this.smartMeteringInstallationClient.decoupleMbusDevice(request);

    this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
  }

  @Then("^the Decouple response is \"([^\"]*)\"$")
  public void theDecoupleResponseIs(final String status) throws WebServiceSecurityException {

    final DecoupleMbusDeviceAsyncRequest decoupleMbusDeviceAsyncRequest =
        DecoupleMbusDeviceRequestFactory.fromScenarioContext();
    final DecoupleMbusDeviceResponse response =
        this.smartMeteringInstallationClient.getDecoupleMbusDeviceResponse(
            decoupleMbusDeviceAsyncRequest);

    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(status);
  }

  @Then("^the Decouple response is \"([^\"]*)\" and contains$")
  public void theDecoupleResponseIsAndContains(final String status, final List<String> resultList)
      throws WebServiceSecurityException {

    final DecoupleMbusDeviceAsyncRequest decoupleMbusDeviceAsyncRequest =
        DecoupleMbusDeviceRequestFactory.fromScenarioContext();
    final DecoupleMbusDeviceResponse response =
        this.smartMeteringInstallationClient.getDecoupleMbusDeviceResponse(
            decoupleMbusDeviceAsyncRequest);

    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(status);
    assertThat(this.checkDescription(response.getDescription(), resultList))
        .as("Description should contain all of " + resultList)
        .isTrue();
  }

  @Then("^retrieving the Decouple response results in an exception$")
  public void retrievingTheDecoupleResponseResultsInAnException()
      throws WebServiceSecurityException {

    final DecoupleMbusDeviceAsyncRequest asyncRequest =
        DecoupleMbusDeviceRequestFactory.fromScenarioContext();

    try {
      this.smartMeteringInstallationClient.getDecoupleMbusDeviceResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }
}
