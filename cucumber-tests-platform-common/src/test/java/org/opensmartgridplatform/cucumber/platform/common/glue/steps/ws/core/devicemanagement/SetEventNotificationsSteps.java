/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.EventNotificationType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class SetEventNotificationsSteps extends GlueBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventNotificationsSteps.class);

    @Autowired
    private CoreDeviceManagementClient client;

    /**
     * Send an event notification request to the Platform
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a set event notification message request(?: on OSGP)?$")
    public void receivingASetEventNotificationMessageRequest(final Map<String, String> requestParameters)
            throws Throwable {
        final SetEventNotificationsRequest request = new SetEventNotificationsRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        for (final String event : getString(requestParameters, PlatformKeys.KEY_EVENT).split(",")) {
            request.getEventNotifications().add(Enum.valueOf(EventNotificationType.class, event.trim()));
        }

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setEventNotifications(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @apiNote The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set event notification async response contains$")
    public void theSetEventNotificationAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
        final SetEventNotificationsAsyncResponse asyncResponse = (SetEventNotificationsAsyncResponse) ScenarioContext
                .current().get(PlatformKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a set event notification response message for device \"([^\"]*)\"")
    public void thePlatformBuffersASetEventNotificationResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetEventNotificationsAsyncRequest request = new SetEventNotificationsAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            SetEventNotificationsResponse response = null;
            try {
                response = this.client.getSetEventNotificationsResponse(request);
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformKeys.KEY_RESULT)),
                    response.getResult());
        });
    }
}