/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.DisableDebuggingRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.EnableDebuggingRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableAndDisableDebugging {

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired
  private SmartMeteringManagementRequestClient<EnableDebuggingAsyncResponse, EnableDebuggingRequest>
      smartMeteringManagementRequestClientEnableDebugging;

  @Autowired
  private SmartMeteringManagementResponseClient<
          EnableDebuggingResponse, EnableDebuggingAsyncRequest>
      smartMeteringManagementResponseClientEnableDebugging;

  @Autowired
  private SmartMeteringManagementRequestClient<
          DisableDebuggingAsyncResponse, DisableDebuggingRequest>
      smartMeteringManagementRequestClientDisableDebugging;

  @Autowired
  private SmartMeteringManagementResponseClient<
          DisableDebuggingResponse, DisableDebuggingAsyncRequest>
      smartMeteringManagementResponseClientDisableDebugging;

  @When("^the enable Debug request is received$")
  public void theEnableDebugRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {

    final EnableDebuggingRequest enableDebuggingRequest =
        EnableDebuggingRequestFactory.fromParameterMap(requestData);
    final EnableDebuggingAsyncResponse enableDebuggingAsyncResponse =
        this.smartMeteringManagementRequestClientEnableDebugging.doRequest(enableDebuggingRequest);

    assertThat(enableDebuggingAsyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            enableDebuggingAsyncResponse.getCorrelationUid());
  }

  @Then("^the device debug information should be enabled$")
  public void theDeviceDebugInformationShouldBeEnabled() throws Throwable {
    final DlmsDevice device =
        this.dlmsDeviceRepository.findByDeviceIdentification(
            ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString());

    assertThat(device.isInDebugMode()).as("Debug mode").isTrue();
  }

  @Then("^the enable debug response should be \"([^\"]*)\"$")
  public void theEnableDebugResponseShouldBe(final String result) throws Throwable {
    final EnableDebuggingAsyncRequest enableDebuggingAsyncRequest =
        EnableDebuggingRequestFactory.fromScenarioContext();
    final EnableDebuggingResponse enableDebuggingResponse =
        this.smartMeteringManagementResponseClientEnableDebugging.getResponse(
            enableDebuggingAsyncRequest);

    assertThat(enableDebuggingResponse)
        .as("EnableDebugRequestResponse should not be null")
        .isNotNull();
    assertThat(enableDebuggingResponse.getResult()).as("Expected results").isNotNull();
  }

  @When("^the disable Debug request is received$")
  public void theDisableDebugRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final DisableDebuggingRequest disableDebuggingRequest =
        DisableDebuggingRequestFactory.fromParameterMap(requestData);
    final DisableDebuggingAsyncResponse disableDebuggingAsyncResponse =
        this.smartMeteringManagementRequestClientDisableDebugging.doRequest(
            disableDebuggingRequest);

    assertThat(disableDebuggingAsyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            disableDebuggingAsyncResponse.getCorrelationUid());
  }

  @Then("^the device debug information should be disabled$")
  public void theDeviceDebugInformationShouldBeDisabled() throws Throwable {
    final DlmsDevice device =
        this.dlmsDeviceRepository.findByDeviceIdentification(
            ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString());

    assertThat(device.isInDebugMode()).as("Debug mode").isFalse();
  }

  @Then("^the disable debug response should be \"([^\"]*)\"$")
  public void theDisableDebugResponseShouldBe(final String result) throws Throwable {
    final DisableDebuggingAsyncRequest disableDebuggingAsyncRequest =
        DisableDebuggingRequestFactory.fromScenarioContext();
    final DisableDebuggingResponse disableDebuggingResponse =
        this.smartMeteringManagementResponseClientDisableDebugging.getResponse(
            disableDebuggingAsyncRequest);

    assertThat(disableDebuggingResponse)
        .as("DisableDebugRequestResponse should not be null")
        .isNotNull();

    assertThat(disableDebuggingResponse.getResult()).as("Expected result").isNotNull();
  }
}
