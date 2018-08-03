/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.MbusChannelShortEquipmentIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.MbusShortEquipmentIdentifier;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.ScanMbusChannelsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ScanMbusChannelsSteps {

    @Autowired
    private SmartMeteringAdHocRequestClient<ScanMbusChannelsAsyncResponse, ScanMbusChannelsRequest> requestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<ScanMbusChannelsResponse, ScanMbusChannelsAsyncRequest> responseClient;

    @When("^the scan M-Bus channels request is received$")
    public void theScanMBusChannelsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ScanMbusChannelsRequest request = ScanMbusChannelsRequestFactory.fromParameterMap(settings);
        final ScanMbusChannelsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the found M-bus devices are in the response$")
    public void theFoundMBusDevicesAreInTheResponse(final Map<String, String> settings) throws Throwable {
        final ScanMbusChannelsAsyncRequest asyncRequest = ScanMbusChannelsRequestFactory.fromScenarioContext();
        final ScanMbusChannelsResponse response = this.responseClient.getResponse(asyncRequest);
        assertEquals("Result is not as expected.", settings.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        this.assertChannelShortIds(settings, response.getChannelShortIds());
    }

    public void assertChannelShortIds(final Map<String, String> expectedValues,
            final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

        for (int channel = 1; channel <= 4; channel++) {
            this.assertShortIdForChannel(expectedValues, channel, channelShortIds);
        }
    }

    private void assertShortIdForChannel(final Map<String, String> expectedValues, final int channel,
            final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

        final String channelPrefix = "Channel" + channel;
        final String expectedIdentificationNumber = expectedValues
                .get(channelPrefix + PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER);
        if (expectedIdentificationNumber == null) {
            /*
             * If no identification number is specified for the channel, do not
             * verify values from the response for it.
             */
            return;
        }
        final String expectedManufacturerIdentification = SettingsHelper.getNonBlankStringValue(expectedValues,
                channelPrefix + PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION);
        final Short expectedVersion = SettingsHelper.getShortValue(expectedValues,
                channelPrefix + PlatformSmartmeteringKeys.MBUS_VERSION);
        final Short expectedDeviceTypeIdentification = SettingsHelper.getShortValue(expectedValues,
                channelPrefix + PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION);

        final MbusShortEquipmentIdentifier shortId = this.findShortIdForChannel(channel, channelShortIds);
        assertNotNull("An M-Bus Short ID is expected for channel " + channel, shortId);

        assertEquals("M-Bus identification number for channel " + channel, expectedIdentificationNumber,
                shortId.getIdentificationNumber());
        assertEquals("M-Bus manufacturer identification for channel " + channel, expectedManufacturerIdentification,
                shortId.getManufacturerIdentification());
        assertEquals("M-Bus version for channel " + channel, expectedVersion, shortId.getVersionIdentification());
        assertEquals("M-Bus device type identification for channel " + channel, expectedDeviceTypeIdentification,
                shortId.getDeviceTypeIdentification());
    }

    private MbusShortEquipmentIdentifier findShortIdForChannel(final int channel,
            final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

        final Predicate<MbusChannelShortEquipmentIdentifier> channelMatches = channelShortId -> channel == channelShortId
                .getChannel();
        return channelShortIds.stream().filter(channelMatches).map(MbusChannelShortEquipmentIdentifier::getShortId)
                .findFirst().orElse(null);
    }
}
