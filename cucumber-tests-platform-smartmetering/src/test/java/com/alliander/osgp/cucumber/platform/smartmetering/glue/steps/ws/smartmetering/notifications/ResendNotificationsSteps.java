/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResendNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationsSteps.class);

    @Value("${smartmetering.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;

    @Value("${smartmetering.response.wait.fail.duration:120000}")
    private int waitFailMillis;

    @Autowired
    private ResponseDataRepository responseDataRespository;

    @When("^the missed notification is resent$")
    public void theMissedNotificationIsResent(final Map<String, String> settings) throws Throwable {
        // Do nothing - scheduled task runs automatically
    }

    @When("^no notification is resent$")
    public void noNotificationIsResend() throws Throwable {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^the response data has values$")
    public void theResponseDataHasValues(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

        final int maxtime = this.waitFailMillis;
        final int timeout = this.waitCheckIntervalMillis;
        final int initial_timeout = 60000; // needed to make sure the ResendNotificationJob has at least runned once

        try {
            Thread.sleep(initial_timeout);
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }

        for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
            try {
                Thread.sleep(timeout);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }
            responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
            if (settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)
                    .equals(responseData.getNumberOfNotificationsSent().toString())) {
                break;
            }
        }

        assertEquals("NumberOfNotificationsSent is not as expected",
                settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT),
                responseData.getNumberOfNotificationsSent().toString());
        assertEquals("MessageType is not as expected", settings.get(PlatformKeys.KEY_MESSAGE_TYPE),
                responseData.getMessageType());
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
        assertEquals("NumberOfNotificationsSentd is not as expected",
                settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT),
                responseData.getNumberOfNotificationsSent());
    }
}
