/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.SetDeviceLifecycleStatusByChannelRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetDeviceLifecycleStatusByChannel {

    @Autowired
    private SmartMeteringManagementRequestClient<SetDeviceLifecycleStatusByChannelAsyncResponse, SetDeviceLifecycleStatusByChannelRequest> smManagementRequestClient;

    @Autowired
    private SmartMeteringManagementResponseClient<SetDeviceLifecycleStatusByChannelResponse, SetDeviceLifecycleStatusByChannelAsyncRequest> smManagementResponseClient;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    private static final String OPERATION = "Set device lifecycle status by channel";

    @When("^a set device lifecycle status by channel request is received$")
    public void aSetDeviceLifecycleStatusByChannelRequestIsReceived(final Map<String, String> settings)
            throws Throwable {

        final SetDeviceLifecycleStatusByChannelRequest request = SetDeviceLifecycleStatusByChannelRequestFactory
                .fromParameterMap(settings);

        final SetDeviceLifecycleStatusByChannelAsyncResponse asyncResponse = this.smManagementRequestClient
                .doRequest(request);

        assertNotNull("setDeviceCommunicationSettingsAsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the set device lifecycle status by channel response is returned$")
    public void theSetDeviceLifecycleStatusByChannelResponseIsReturned(final Map<String, String> settings)
            throws Throwable {

        final SetDeviceLifecycleStatusByChannelAsyncRequest asyncRequest = SetDeviceLifecycleStatusByChannelRequestFactory
                .fromScenarioContext();
        final SetDeviceLifecycleStatusByChannelResponse response = this.smManagementResponseClient
                .getResponse(asyncRequest);

        assertEquals(OPERATION + ", Checking result:", OsgpResultType.OK, response.getResult());
        assertEquals(OPERATION + ", Checking gatewayDeviceId:",
                settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION),
                response.getSetDeviceLifecycleStatusByChannelResponseData().getGatewayDeviceIdentification());
        assertEquals(OPERATION + ", Checking channel:",
                Short.parseShort(settings.get(PlatformSmartmeteringKeys.CHANNEL)),
                response.getSetDeviceLifecycleStatusByChannelResponseData().getChannel());

        final SmartMeter gatewayDevice = this.smartMeterRepository
                .findByDeviceIdentification(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        final List<SmartMeter> mbusDevices = this.smartMeterRepository.getMbusDevicesForGateway(gatewayDevice.getId());
        SmartMeter mbusDevice = null;
        for (final SmartMeter device : mbusDevices) {
            if (device.getChannel().equals(Short.parseShort(settings.get(PlatformSmartmeteringKeys.CHANNEL)))) {
                mbusDevice = device;
                break;
            }
        }

        assertEquals(OPERATION + ", Checking mbusDeviceIdentification:",
                response.getSetDeviceLifecycleStatusByChannelResponseData().getMbusDeviceIdentification(),
                mbusDevice.getDeviceIdentification());
        assertEquals(OPERATION + ", Checking deviceLifecycleStatus of device:",
                DeviceLifecycleStatus.valueOf(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_LIFECYCLE_STATUS)),
                mbusDevice.getDeviceLifecycleStatus());
    }

    @Then("^set device lifecycle status by channel request should return an exception$")
    public void setDeviceLifecycleStatusByChannelRequestShouldReturnAnException() throws Throwable {

        final SetDeviceLifecycleStatusByChannelAsyncRequest asyncRequest = SetDeviceLifecycleStatusByChannelRequestFactory
                .fromScenarioContext();
        try {
            this.smManagementResponseClient.getResponse(asyncRequest);
            fail("A SoapFaultClientException should be thrown.");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }

    }

}
