/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notification;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.Notification;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;

public class SmartMeteringNotificationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringNotificationSteps.class);

    /*
     * Allow a little more time than the period for the job trigger to re-send
     * notifications.
     */
    private static final int MAX_WAIT_FOR_NOTIFICATION = 65_000;

    @Autowired
    private NotificationService notificationService;

    @Then("^a notification is sent$")
    public void aNotificationIsSent() throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            Assertions.fail("No " + PlatformKeys.KEY_CORRELATION_UID
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }
        this.waitForNotification(MAX_WAIT_FOR_NOTIFICATION, correlationUid, true);
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent() throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            Assertions.fail("No " + PlatformKeys.KEY_CORRELATION_UID
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }
        this.waitForNotification(MAX_WAIT_FOR_NOTIFICATION, correlationUid, false);
    }

    private void waitForNotification(final int maxTimeOut, final String correlationUid,
            final boolean expectCorrelationUid) throws Throwable {

        LOGGER.info(
                "Waiting to make sure {} notification is received for correlation UID {} for at most {} milliseconds.",
                expectCorrelationUid ? "a" : "no", correlationUid, maxTimeOut);

        final Notification notification = this.notificationService.getNotification(correlationUid, maxTimeOut,
                TimeUnit.MILLISECONDS);

        final boolean gotExpectedNotification = expectCorrelationUid && notification != null;
        final boolean didNotGetUnexpectedNotification = !expectCorrelationUid && notification == null;
        if (gotExpectedNotification || didNotGetUnexpectedNotification) {
            return;
        }

        if (expectCorrelationUid) {
            Assertions.fail("Did not receive a notification for correlation UID: " + correlationUid + " within "
                    + maxTimeOut + " milliseconds");
        } else {
            Assertions.fail("Received notification for correlation UID: " + correlationUid);
        }
    }
}
