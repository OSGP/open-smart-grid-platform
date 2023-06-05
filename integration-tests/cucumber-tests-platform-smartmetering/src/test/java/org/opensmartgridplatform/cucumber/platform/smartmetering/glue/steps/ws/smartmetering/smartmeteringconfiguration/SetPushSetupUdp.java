/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.PUSH_SETUP;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigInteger;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupUdpRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetPushSetupUdp {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupUdp.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupUdp request is received$")
  public void theSetPushSetupUdpRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupUdpRequest setPushSetupUdpRequest =
        SetPushSetupUdpRequestFactory.fromParameterMap(settings);
    final SetPushSetupUdpAsyncResponse setPushSetupUdpAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupUdp(setPushSetupUdpRequest);

    LOGGER.info("Set push setup Udp response is received {}", setPushSetupUdpAsyncResponse);
    assertThat(setPushSetupUdpAsyncResponse)
        .as("Set push setup Udp response should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, setPushSetupUdpAsyncResponse.getCorrelationUid());
  }

  @Then("^the PushSetupUdp response should be returned$")
  public void thePushSetupUdpResponseIs(final Map<String, String> settings) throws Throwable {
    final SetPushSetupUdpAsyncRequest setPushSetupUdpAsyncRequest =
        SetPushSetupUdpRequestFactory.fromScenarioContext();
    final SetPushSetupUdpResponse setPushSetupUdpResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupUdpResponse(
            setPushSetupUdpAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);

    assertThat(setPushSetupUdpResponse).isNotNull();
    assertThat(setPushSetupUdpResponse.getResult()).isNotNull();
    assertThat(setPushSetupUdpResponse.getResult())
        .isEqualTo(OsgpResultType.valueOf(expectedResult));
  }

  @Then("^the PushSetupUdp should be set on the device$")
  public void thePushSetupUdpShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    final GetSpecificAttributeValueResponse specificAttributeValues =
        this.getSpecificAttributeValues(settings);
    assertThat(specificAttributeValues)
        .as("GetSpecificAttributeValuesResponse was null")
        .isNotNull();
    assertThat(specificAttributeValues.getResult())
        .as("GetSpecificAttributeValuesResponse result was null")
        .isNotNull();
    assertThat(specificAttributeValues.getResult())
        .as("GetSpecificAttributeValuesResponse should be OK")
        .isEqualTo(OsgpResultType.OK);

    final String actual = specificAttributeValues.getAttributeValueData();
    assertThat(actual)
        .isEqualTo(
            "DataObject: Choice=ARRAY, ResultData isComplex, value=[java.util.LinkedList]: [\n]\n");
  }

  private GetSpecificAttributeValueResponse getSpecificAttributeValues(
      final Map<String, String> settings) throws WebServiceSecurityException {

    // Make a specificAttributeValueRequest to read out the register for
    // pushsetupUdp on the device simulator. ClassID = 40, ObisCode =
    // 0.3.25.9.0.255 and AttributeID = 4. This is the attribute that stores
    // 'communication_window' which we sent earlier in the form of
    // host:port

    final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequest();
    request.setClassId(BigInteger.valueOf(PUSH_SETUP.id()));
    request.setObisCode(this.getObisCodeValues());
    request.setAttribute(BigInteger.valueOf(PushSetupAttribute.COMMUNICATION_WINDOW.attributeId()));
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final GetSpecificAttributeValueAsyncResponse asyncResponse =
        this.adHocRequestclient.doRequest(request);
    final GetSpecificAttributeValueAsyncRequest asyncRequest =
        new GetSpecificAttributeValueAsyncRequest();
    asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
    asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
    return this.adHocResponseClient.getResponse(asyncRequest);
  }

  private ObisCodeValues getObisCodeValues() {
    final ObisCodeValues values = new ObisCodeValues();
    values.setA((short) 0);
    values.setB((short) 3);
    values.setC((short) 25);
    values.setD((short) 9);
    values.setE((short) 0);
    values.setF((short) 255);

    return values;
  }
}
