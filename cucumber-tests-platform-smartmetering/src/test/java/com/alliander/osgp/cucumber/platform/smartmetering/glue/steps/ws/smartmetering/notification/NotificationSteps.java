/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notification;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications.ResendNotificationJobSteps;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.notification.NotificationService;

import cucumber.api.java.en.Then;

public class NotificationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationJobSteps.class);

    /*
     * Allow a little more time than the period for the job trigger to re-send
     * notifications.
     */
    private static final int MAX_WAIT_FOR_NOTIFICATION = 65000;

    @Autowired
    private NotificationService notificationService;

    @Then("^a notification is sent$")
    public void aNotificationIsSent() throws Throwable {

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            fail("No " + PlatformKeys.KEY_CORRELATION_UID
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }

        try {
            this.notificationService.getNotification(correlationUid).get(MAX_WAIT_FOR_NOTIFICATION,
                    TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(
                    "Thread was interrupted while awaiting notification for correlation UID: " + correlationUid, e);
        } catch (final ExecutionException e) {
            throw new AssertionError("Exception while obtaining notification for correlation UID: " + correlationUid,
                    e);
        } catch (final TimeoutException e) {
            fail("A notification for correlation UID " + correlationUid + " has not been sent within "
                    + MAX_WAIT_FOR_NOTIFICATION + " milliseconds.");
        }
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent() throws Throwable {

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            fail("No " + PlatformKeys.KEY_CORRELATION_UID
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }

        try {
            final Notification notification = this.notificationService.getNotification(correlationUid)
                    .get(MAX_WAIT_FOR_NOTIFICATION, TimeUnit.MILLISECONDS);
            fail("A notification for correlation UID " + correlationUid + " for notification type "
                    + notification.getNotificationType() + " was sent.");
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(
                    "Thread was interrupted while awaiting no notification to be sent for correlation UID: "
                            + correlationUid,
                    e);
        } catch (final ExecutionException e) {
            throw new AssertionError(
                    "Exception while obtaining the notification not to be sent for correlation UID: " + correlationUid,
                    e);
        } catch (final TimeoutException e) {
            LOGGER.debug(
                    "Got expected time-out waiting for notification that should not be sent for correlation UID: {} -> {}",
                    correlationUid, e);
        }
    }
}
