/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.platform.cucumber.config.CorePersistenceConfig;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class ReceiveEventNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveEventNotificationsSteps.class);

    @Autowired
    private CorePersistenceConfig configuration;

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send an event notification request to the Platform
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a receive event notification message request(?: on OSGP)?$")
    public void receiving_a_receive_event_notification_message_request(final Map<String, String> requestParameters)
            throws Throwable {
        final ReceiveEventNotificationsRequest request = new ReceiveEventNotificationsRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.receiveEventNotifications(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the receive event notification async response contains$")
    public void the_receive_event_notification_async_response_contains(final Map<String, String> expectedResponseData)
            throws Throwable {
        final ReceiveEventNotificationsAsyncResponse response = (ReceiveEventNotificationsAsyncResponse) ScenarioContext
                .Current().get(Keys.RESPONSE);

        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a receive event notification response message for device \"([^\"]*)\"")
    public void the_platform_buffers_a_receive_event_notification_response_message_for_device(
            final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
        final ReceiveEventNotificationsAsyncRequest request = new ReceiveEventNotificationsAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getDefaultTimeout()) {
                Assert.fail("Timeout");
            }

            count++;

            try {
                final ReceiveEventNotificationsResponse response = this.client
                        .getReceiveEventNotificationsResponse(request);

                Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
                        response.getResult());

                success = true;
            } catch (final Exception ex) {
                LOGGER.debug(ex.getMessage());
            }
        }
    }
}