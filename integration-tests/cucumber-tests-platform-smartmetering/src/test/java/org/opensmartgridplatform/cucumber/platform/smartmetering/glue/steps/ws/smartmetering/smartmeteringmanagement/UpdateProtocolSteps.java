/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo.Builder;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateProtocolSteps {
  @Autowired
  private SmartMeteringManagementRequestClient<UpdateProtocolAsyncResponse, UpdateProtocolRequest>
      smManagementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<UpdateProtocolResponse, UpdateProtocolAsyncRequest>
      smManagementResponseClient;

  @Autowired private SmartMeterRepository smartMeterRepository;
  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;
  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Given("a protocol exists in the database")
  public void aProtocolInTheDatabase(final Map<String, String> settings) {
    final ProtocolInfo found =
        this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            getProtocol(settings), getProtocolVersion(settings), getProtocolVariant(settings));

    if (found == null) {
      this.protocolInfoRepository.save(this.getProtocolFromMap(settings));
    }
  }

  @When("an update protocol request is received")
  public void whenReceivingUpdateDeviceProtocolRequest(final Map<String, String> input)
      throws WebServiceSecurityException {
    final UpdateProtocolRequest request = new UpdateProtocolRequest();
    request.setDeviceIdentification(getString(input, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setUpdateProtocolData(getProtocolData(input));

    final UpdateProtocolAsyncResponse asyncResponse =
        this.smManagementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("UpdateProtocolAsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("the update protocol response should be returned")
  public void thenTheUpdateDeviceProtocolResponseShouldBeReturned() throws Throwable {
    final UpdateProtocolAsyncRequest asyncRequest = new UpdateProtocolAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    final UpdateProtocolResponse response =
        this.smManagementResponseClient.getResponse(asyncRequest);

    assertThat(response.getResult())
        .as("UpdateProtocol checking result:")
        .isEqualTo(OsgpResultType.OK);
  }

  @Then("the core device is configured with the protocol")
  public void thenTheCoreDeviceIsConfiguredWithTheProtocol(final Map<String, String> input) {
    final String deviceIdentification = getString(input, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Device coreDevice =
        this.smartMeterRepository.findByDeviceIdentification(deviceIdentification);

    final ProtocolInfo protocolInfo = coreDevice.getProtocolInfo();

    final UpdateProtocolData expected = getProtocolData(input);

    assertThat(protocolInfo.getProtocol()).isEqualTo(expected.getProtocol());
    assertThat(protocolInfo.getProtocolVersion()).isEqualTo(expected.getProtocolVersion());
    assertThat(protocolInfo.getProtocolVariant()).isEqualTo(expected.getProtocolVariant());
  }

  @Then("the dlms device is configured with the protocol")
  public void thenTheDlmsDeviceIsConfiguredWithTheProtocol(final Map<String, String> input) {
    final String deviceIdentification = getString(input, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

    final String expectedProtocolName = getProtocol(input);
    final String expectedProtocolVersion = getProtocolVersion(input);

    assertThat(dlmsDevice.getProtocolName()).isEqualTo(expectedProtocolName);
    assertThat(dlmsDevice.getProtocolVersion()).isEqualTo(expectedProtocolVersion);
  }

  private static UpdateProtocolData getProtocolData(final Map<String, String> input) {
    final UpdateProtocolData protocolData = new UpdateProtocolData();
    protocolData.setProtocol(getProtocol(input));
    protocolData.setProtocolVersion(getProtocolVersion(input));
    protocolData.setProtocolVariant(getProtocolVariant(input));
    return protocolData;
  }

  private static String getProtocol(final Map<String, String> settings) {
    return getString(settings, PlatformKeys.KEY_PROTOCOL);
  }

  private static String getProtocolVersion(final Map<String, String> settings) {
    return getString(settings, PlatformKeys.KEY_PROTOCOL_VERSION);
  }

  private static String getProtocolVariant(final Map<String, String> settings) {
    return getString(
        settings, PlatformKeys.KEY_PROTOCOL_VARIANT, PlatformDefaults.DEFAULT_PROTOCOL_VARIANT);
  }

  private ProtocolInfo getProtocolFromMap(final Map<String, String> settings) {
    final Builder builder = new ProtocolInfo.Builder();
    builder.withProtocol(getProtocol(settings));
    builder.withProtocolVersion(getProtocolVersion(settings));
    builder.withProtocolVariant(getProtocolVariant(settings));
    builder.withOutgoingRequestsPropertyPrefix("property");
    builder.withIncomingResponsesPropertyPrefix("property");
    builder.withOutgoingResponsesPropertyPrefix("property");
    builder.withIncomingRequestsPropertyPrefix("property");
    return builder.build();
  }
}
