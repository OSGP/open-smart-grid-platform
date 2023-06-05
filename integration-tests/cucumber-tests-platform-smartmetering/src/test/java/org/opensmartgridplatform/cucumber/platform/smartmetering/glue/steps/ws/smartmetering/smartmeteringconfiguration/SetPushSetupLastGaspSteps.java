// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupLastGaspRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SetPushSetupLastGaspSteps {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupLastGasp request is received$")
  public void theSetPushSetupLastGaspRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupLastGaspRequest setPushSetupLastGaspRequest =
        SetPushSetupLastGaspRequestFactory.fromParameterMap(settings);
    final SetPushSetupLastGaspAsyncResponse setPushSetupLastGaspAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupLastGasp(setPushSetupLastGaspRequest);

    log.info("Set push setup LastGasp response is received {}", setPushSetupLastGaspAsyncResponse);
    assertThat(setPushSetupLastGaspAsyncResponse)
        .as("Set push setup LastGasp response should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setPushSetupLastGaspAsyncResponse.getCorrelationUid());
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.HOSTNAME, settings.get(PlatformSmartmeteringKeys.HOSTNAME));
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.PORT, settings.get(PlatformSmartmeteringKeys.PORT));
  }

  @Then("^the PushSetupLastGasp should be set on the device$")
  public void thePushSetupLastGaspShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupLastGaspAsyncRequest setPushSetupLastGaspAsyncRequest =
        SetPushSetupLastGaspRequestFactory.fromScenarioContext();
    final SetPushSetupLastGaspResponse setPushSetupLastGaspResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupLastGaspResponse(
            setPushSetupLastGaspAsyncRequest);

    assertThat(setPushSetupLastGaspResponse)
        .as("SetPushSetupLastGaspResponse was null")
        .isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult())
        .as("SetPushSetupLastGaspResponse result was null")
        .isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult())
        .as("SetPushSetupLastGaspResponse should be OK")
        .isEqualTo(OsgpResultType.OK);

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

    final String hostAndPort =
        ScenarioContext.current().get(PlatformSmartmeteringKeys.HOSTNAME)
            + ":"
            + ScenarioContext.current().get(PlatformSmartmeteringKeys.PORT);
    final byte[] expectedBytes = (hostAndPort.getBytes(StandardCharsets.US_ASCII));
    final String expected = Arrays.toString(expectedBytes);
    final String actual = specificAttributeValues.getAttributeValueData();
    assertThat(actual.contains(expected)).as("PushSetupLastGasp was not set on device").isTrue();
  }

  private GetSpecificAttributeValueResponse getSpecificAttributeValues(
      final Map<String, String> settings)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {

    // Make a specificAttributeValueRequest to read out the register for
    // pushsetuplastgasp on the device simulator. ClassID = 40, ObisCode =
    // 0.3.25.9.0.255 and AttributeID = 3. This is the attribute that stores
    // 'sendDestinationAndMethod' which we sent earlier in the form of
    // host:port

    final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequest();
    request.setClassId(BigInteger.valueOf(40L));
    request.setObisCode(this.getObisCodeValues());
    request.setAttribute(BigInteger.valueOf(3L));
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
