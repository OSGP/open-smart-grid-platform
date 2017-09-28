/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupSmsRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

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

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private SimulatorTriggerClient simulatorTriggerClient;

    private static final BigInteger CLASS_ID = BigInteger.valueOf(40L);
    private static final int ATTRIBUTE_ID = 3;

    @When("^the set PushSetupSms request is received$")
    public void theSetPushSetupSmsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final SetPushSetupSmsRequest smsrequest = SetPushSetupSmsRequestFactory.fromParameterMap(settings);

        final SetPushSetupSmsAsyncResponse smsasyncResponse = this.smartMeteringConfigurationClient
                .setPushSetupSms(smsrequest);

        LOGGER.info("Set special days response is received {}", smsasyncResponse);

        assertNotNull("Set special days response should not be null", smsasyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                smsasyncResponse.getCorrelationUid());
        ScenarioContext.current().put(PlatformSmartmeteringKeys.HOSTNAME,
                settings.get(PlatformSmartmeteringKeys.HOSTNAME));
        ScenarioContext.current().put(PlatformSmartmeteringKeys.PORT, settings.get(PlatformSmartmeteringKeys.PORT));
    }

    @Then("^the PushSetupSms should be set on the device$")
    public void thePushSetupSmsShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {

        final SetPushSetupSmsAsyncRequest asyncRequest = SetPushSetupSmsRequestFactory.fromScenarioContext();
        final SetPushSetupSmsResponse response = this.smartMeteringConfigurationClient
                .getSetPushSetupSmsResponse(asyncRequest);

        assertNotNull("SetPushSetupsSmsResponse was null", response);
        assertNotNull("SetPushSetupsSmsResponse result was null", response.getResult());
        assertEquals("SetPushSetupsSmsResponse should be OK", OsgpResultType.OK, response.getResult());

        final GetSpecificAttributeValueResponse specificAttributeValues = this.getSpecificAttributeValues(settings);
        assertNotNull("GetSpecificAttributeValuesResponse was null", specificAttributeValues);
        assertNotNull("GetSpecificAttributeValuesResponse result was null", response.getResult());
        assertEquals("GetSpecificAttributeValuesResponse should be OK", OsgpResultType.OK, response.getResult());

        final String hostAndPort = (String) ScenarioContext.current().get(PlatformSmartmeteringKeys.HOSTNAME) + ":"
                + ScenarioContext.current().get(PlatformSmartmeteringKeys.PORT);
        final byte[] expectedBytes = (hostAndPort.getBytes(StandardCharsets.US_ASCII));
        final String expected = Arrays.toString(expectedBytes);
        final String actual = new String(specificAttributeValues.getAttributeValueData().toString());
        assertTrue("PushSetupSms was not set on device", actual.toString().contains(expected));
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

        GetSpecificAttributeValueAsyncResponse asyncResponse;
        asyncResponse = this.adHocRequestclient.doRequest(request);
        final GetSpecificAttributeValueAsyncRequest asyncRequest = new GetSpecificAttributeValueAsyncRequest();
        asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
        asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
        return this.adHocResponseClient.getResponse(asyncRequest);
    }

    /*
     * Returns the obiscodevalues for pushSetupSms
     */
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
