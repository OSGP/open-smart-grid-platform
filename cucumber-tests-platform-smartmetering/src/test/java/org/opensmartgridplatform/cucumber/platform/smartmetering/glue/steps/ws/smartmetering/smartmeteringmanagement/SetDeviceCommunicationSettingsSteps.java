/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SetDeviceCommunicationSettingsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetDeviceCommunicationSettingsSteps {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private SmartMeteringManagementRequestClient<SetDeviceCommunicationSettingsAsyncResponse, SetDeviceCommunicationSettingsRequest> smManagementRequestClientSetDeviceCommunicationSettings;

    @Autowired
    private SmartMeteringManagementResponseClient<SetDeviceCommunicationSettingsResponse, SetDeviceCommunicationSettingsAsyncRequest> smManagementResponseClientSetDeviceCommunicationSettings;

    @When("^the set device communication settings request is received$")
    public void theSetDeviceCommunicationSettingsRequestIsReceived(final Map<String, String> requestData)
            throws Throwable {

        final SetDeviceCommunicationSettingsRequest setDeviceCommunicationSettingsRequest = SetDeviceCommunicationSettingsRequestFactory
                .fromParameterMap(requestData);

        final SetDeviceCommunicationSettingsAsyncResponse setDeviceCommunicationSettingsAsyncResponse = this.smManagementRequestClientSetDeviceCommunicationSettings
                .doRequest(setDeviceCommunicationSettingsRequest);

        assertNotNull("setDeviceCommunicationSettingsAsyncResponse should not be null",
                setDeviceCommunicationSettingsAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setDeviceCommunicationSettingsAsyncResponse.getCorrelationUid());
    }

    @Then("^the set device communication settings response should be \"([^\"]*)\"$")
    public void theSetDeviceCommunicationSettingsResponseShouldBe(final String result) throws Throwable {
        final SetDeviceCommunicationSettingsAsyncRequest setDeviceCommunicationSettingsAsyncRequest = SetDeviceCommunicationSettingsRequestFactory
                .fromScenarioContext();

        final SetDeviceCommunicationSettingsResponse setDeviceCommunicationSettingsResponse = this.smManagementResponseClientSetDeviceCommunicationSettings
                .getResponse(setDeviceCommunicationSettingsAsyncRequest);

        assertNotNull("SetDeviceCommunicationSettingsResponse should not be null",
                setDeviceCommunicationSettingsResponse);
        assertNotNull("Expected OsgpResultType should not be null", setDeviceCommunicationSettingsResponse.getResult());
    }

    @Then("^the device \"([^\"]*)\" should be in the database with attributes$")
    public void theDeviceShouldBeInTheDatabaseWithAttributes(final String deviceIdentification,
            final Map<String, String> settings) throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

        final int expectedResult = getInteger(settings, PlatformSmartmeteringKeys.CHALLENGE_LENGTH,
                PlatformSmartmeteringDefaults.CHALLENGE_LENGTH);
        assertEquals("Number of challenge length should match", expectedResult, device.getChallengeLength().intValue());

        assertEquals("With list supported should match",
                getBoolean(settings, PlatformSmartmeteringKeys.WITH_LIST_SUPPORTED), device.isWithListSupported());
        assertEquals("Selective access supported should match",
                getBoolean(settings, PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED),
                device.isSelectiveAccessSupported());
        assertEquals("IP address is static should match",
                getBoolean(settings, PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC), device.isIpAddressIsStatic());
        assertEquals("Use SN should match", getBoolean(settings, PlatformSmartmeteringKeys.USE_SN), device.isUseSn());
        assertEquals("Use HDLC should match", getBoolean(settings, PlatformSmartmeteringKeys.USE_HDLC),
                device.isUseHdlc());
    }

}
