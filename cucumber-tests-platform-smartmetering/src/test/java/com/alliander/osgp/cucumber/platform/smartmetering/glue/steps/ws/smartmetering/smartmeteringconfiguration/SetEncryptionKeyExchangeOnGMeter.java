/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetEncryptionKeyExchangeOnGMeter {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeter.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private DlmsSecurityKeyRepository dlmsSecurityKeyRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @When("^the exchange user key request is received$")
    public void theExchangeUserKeyRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest = SetEncryptionKeyExchangeOnGMeterRequestFactory
                .fromParameterMap(requestData);

        final SetEncryptionKeyExchangeOnGMeterAsyncResponse setEncryptionKeyExchangeOnGMeterAsyncResponse = this.smartMeteringConfigurationClient
                .setEncryptionKeyExchangeOnGMeter(setEncryptionKeyExchangeOnGMeterRequest);

        LOGGER.info("Set encryptionKey exchange on GMeter response is received {}",
                setEncryptionKeyExchangeOnGMeterAsyncResponse);

        assertNotNull("Set encryptionKey exchange on GMeter response should not be null",
                setEncryptionKeyExchangeOnGMeterAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setEncryptionKeyExchangeOnGMeterAsyncResponse.getCorrelationUid());

    }

    @Then("^the new user key should be set on the gas device$")
    public void theNewUserKeyShouldBeSetOnTheGasDevice(final Map<String, String> settings) throws Throwable {
        final SetEncryptionKeyExchangeOnGMeterAsyncRequest setEncryptionKeyExchangeOnGMeterAsyncRequest = SetEncryptionKeyExchangeOnGMeterRequestFactory
                .fromScenarioContext();
        final SetEncryptionKeyExchangeOnGMeterResponse setEncryptionKeyExchangeOnGMeterResponse = this.smartMeteringConfigurationClient
                .retrieveSetEncryptionKeyExchangeOnGMeterResponse(setEncryptionKeyExchangeOnGMeterAsyncRequest);

        LOGGER.info("Set encryptionKey exchange on GMeter result is: {}",
                setEncryptionKeyExchangeOnGMeterResponse.getResult());

        assertNotNull("Set encryptionKey exchange on GMeter result is null",
                setEncryptionKeyExchangeOnGMeterResponse.getResult());
        assertEquals("Set encryptionKey exchange on GMeter result should be OK", OsgpResultType.OK,
                setEncryptionKeyExchangeOnGMeterResponse.getResult());

        final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

        final SecurityKey oldSecurityKey = this.dlmsSecurityKeyRepository
                .findByDlmsDeviceAndSecurityKeyTypeAndValidToNotNull(dlmsDevice, SecurityKeyType.G_METER_ENCRYPTION);

        final SecurityKey newSecurityKey = this.dlmsSecurityKeyRepository
                .findByDlmsDeviceAndSecurityKeyTypeAndValidToIsNull(dlmsDevice, SecurityKeyType.G_METER_ENCRYPTION);

        assertNotNull("End date of generated user key is null", oldSecurityKey.getValidTo());
        assertNotNull("Generated user key is null", newSecurityKey);
    }

    @Then("^the new user key is generated and should be set on the gas device$")
    public void theNewUserKeyIsGeneratedAndShouldBeSetOnTheGasDevice(final Map<String, String> settings)
            throws Throwable {
        final SetEncryptionKeyExchangeOnGMeterAsyncRequest setEncryptionKeyExchangeOnGMeterAsyncRequest = SetEncryptionKeyExchangeOnGMeterRequestFactory
                .fromScenarioContext();
        final SetEncryptionKeyExchangeOnGMeterResponse setEncryptionKeyExchangeOnGMeterResponse = this.smartMeteringConfigurationClient
                .retrieveSetEncryptionKeyExchangeOnGMeterResponse(setEncryptionKeyExchangeOnGMeterAsyncRequest);

        assertNotNull("Set encryptionKey exchange on GMeter result is null",
                setEncryptionKeyExchangeOnGMeterResponse.getResult());
        assertEquals("Set encryptionKey exchange on GMeter result should be OK", OsgpResultType.OK,
                setEncryptionKeyExchangeOnGMeterResponse.getResult());

        final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

        final SecurityKey securityKey = this.dlmsSecurityKeyRepository
                .findByDlmsDeviceAndSecurityKeyTypeAndValidToIsNull(dlmsDevice, SecurityKeyType.G_METER_ENCRYPTION);

        assertNotNull("Generated user key is null", securityKey);
    }

}
