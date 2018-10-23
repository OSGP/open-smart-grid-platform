/**
  * Copyright 2017 Smart Society Services B.V.
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupSmsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetPushSetupSms {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupSms.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private SmartMeteringAdHocRequestClient<GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest> adHocRequestclient;
    @Autowired
    private SmartMeteringAdHocResponseClient<GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest> adHocResponseClient;

    @When("^the set PushSetupSms request is received$")
    public void theSetPushSetupSmsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final SetPushSetupSmsRequest setPushSetupSmsRequest = SetPushSetupSmsRequestFactory.fromParameterMap(settings);
        final SetPushSetupSmsAsyncResponse setPushSetupSmsAsyncResponse = this.smartMeteringConfigurationClient
                .setPushSetupSms(setPushSetupSmsRequest);

        LOGGER.info("Set push setup sms response is received {}", setPushSetupSmsAsyncResponse);
        assertNotNull("Set push setup sms response should not be null", setPushSetupSmsAsyncResponse);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setPushSetupSmsAsyncResponse.getCorrelationUid());
        ScenarioContext.current().put(PlatformSmartmeteringKeys.HOSTNAME,
                settings.get(PlatformSmartmeteringKeys.HOSTNAME));
        ScenarioContext.current().put(PlatformSmartmeteringKeys.PORT, settings.get(PlatformSmartmeteringKeys.PORT));
    }

    @Then("^the PushSetupSms should be set on the device$")
    public void thePushSetupSmsShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {

        final SetPushSetupSmsAsyncRequest setPushSetupSmsAsyncRequest = SetPushSetupSmsRequestFactory
                .fromScenarioContext();
        final SetPushSetupSmsResponse setPushSetupSmsResponse = this.smartMeteringConfigurationClient
                .getSetPushSetupSmsResponse(setPushSetupSmsAsyncRequest);

        assertNotNull("SetPushSetupSmsResponse was null", setPushSetupSmsResponse);
        assertNotNull("SetPushSetupSmsResponse result was null", setPushSetupSmsResponse.getResult());
        assertEquals("SetPushSetupSmsResponse should be OK", OsgpResultType.OK, setPushSetupSmsResponse.getResult());

        final GetSpecificAttributeValueResponse specificAttributeValues = this.getSpecificAttributeValues(settings);
        assertNotNull("GetSpecificAttributeValuesResponse was null", specificAttributeValues);
        assertNotNull("GetSpecificAttributeValuesResponse result was null", specificAttributeValues.getResult());
        assertEquals("GetSpecificAttributeValuesResponse should be OK", OsgpResultType.OK,
                specificAttributeValues.getResult());

        final String hostAndPort = ScenarioContext.current().get(PlatformSmartmeteringKeys.HOSTNAME) + ":"
                + ScenarioContext.current().get(PlatformSmartmeteringKeys.PORT);
        final byte[] expectedBytes = (hostAndPort.getBytes(StandardCharsets.US_ASCII));
        final String expected = Arrays.toString(expectedBytes);
        final String actual = specificAttributeValues.getAttributeValueData();
        assertTrue("PushSetupSms was not set on device", actual.contains(expected));
    }

    private GetSpecificAttributeValueResponse getSpecificAttributeValues(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        // Make a specificAttributeValueRequest to read out the register for
        // pushsetupsms on the device simulator. ClassID = 40, ObisCode =
        // 0.2.25.9.0.255 and AttributeID = 3. This is the attribute that stores
        // 'sendDestinationAndMethod' which we sent earlier in the form of
        // host:port

        final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequest();
        request.setClassId(BigInteger.valueOf(40L));
        request.setObisCode(this.getObisCodeValues());
        request.setAttribute(BigInteger.valueOf(3L));
        request.setDeviceIdentification(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

        final GetSpecificAttributeValueAsyncResponse asyncResponse = this.adHocRequestclient.doRequest(request);
        final GetSpecificAttributeValueAsyncRequest asyncRequest = new GetSpecificAttributeValueAsyncRequest();
        asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
        asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
        return this.adHocResponseClient.getResponse(asyncRequest);
    }

    private ObisCodeValues getObisCodeValues() {
        final ObisCodeValues values = new ObisCodeValues();
        values.setA((short) 0);
        values.setB((short) 2);
        values.setC((short) 25);
        values.setD((short) 9);
        values.setE((short) 0);
        values.setF((short) 255);

        return values;
    }
}
