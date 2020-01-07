/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
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
import org.springframework.beans.factory.annotation.Autowired;

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

        assertThat(setDeviceCommunicationSettingsAsyncResponse)
                .as("setDeviceCommunicationSettingsAsyncResponse should not be null").isNotNull();
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setDeviceCommunicationSettingsAsyncResponse.getCorrelationUid());
    }

    @Then("^the set device communication settings response should be \"([^\"]*)\"$")
    public void theSetDeviceCommunicationSettingsResponseShouldBe(final String result) throws Throwable {
        final SetDeviceCommunicationSettingsAsyncRequest setDeviceCommunicationSettingsAsyncRequest = SetDeviceCommunicationSettingsRequestFactory
                .fromScenarioContext();

        final SetDeviceCommunicationSettingsResponse setDeviceCommunicationSettingsResponse = this.smManagementResponseClientSetDeviceCommunicationSettings
                .getResponse(setDeviceCommunicationSettingsAsyncRequest);

        assertThat(setDeviceCommunicationSettingsResponse)
                .as("SetDeviceCommunicationSettingsResponse should not be null").isNotNull();
        assertThat(setDeviceCommunicationSettingsResponse.getResult()).as("Expected OsgpResultType should not be null")
                .isNotNull();
    }

    @Then("^the device \"([^\"]*)\" should be in the database with attributes$")
    public void theDeviceShouldBeInTheDatabaseWithAttributes(final String deviceIdentification,
            final Map<String, String> settings) throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

        final int expectedResult = getInteger(settings, PlatformSmartmeteringKeys.CHALLENGE_LENGTH,
                PlatformSmartmeteringDefaults.CHALLENGE_LENGTH);

        assertThat(device.getChallengeLength().intValue()).as("Number of challenge length should match")
                .isEqualTo(expectedResult);
        assertThat(device.isWithListSupported()).as("With list supported should match")
                .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.WITH_LIST_SUPPORTED));
        assertThat(device.isSelectiveAccessSupported()).as("Selective access supported should match")
                .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED));
        assertThat(device.isIpAddressIsStatic()).as("IP address is static should match")
                .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC));
        assertThat(device.isUseSn()).as("Use SN should match")
                .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.USE_SN));
        assertThat(device.isUseHdlc()).as("Use HDLC should match")
                .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.USE_HDLC));
    }

}
