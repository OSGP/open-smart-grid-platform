/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.notification;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.Notification;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;

public class PublicLightingNotificationSteps {

    private static final int MAX_WAIT_FOR_NOTIFICATION = 65_000;
    /*
     * Unknown notification means a notification for a correlation UID that has
     * not been captured earlier on. This might be because it is a device
     * initiated notification, or a notification about a request initiated from
     * application code instead of test code as happens when re-establishing an
     * RTU connection.
     */
    private static final int MAX_WAIT_FOR_UNKNOWN_NOTIFICATION = 200_000;

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingNotificationSteps.class);

    @Autowired
    private PublicLightingNotificationService notificationService;

    @Then("^I should receive a notification$")
    public void iShouldReceiveANotification() {
        LOGGER.info("Waiting for a notification for at most {} milliseconds.", MAX_WAIT_FOR_UNKNOWN_NOTIFICATION);

        final Notification notification = this.notificationService.getNotification(MAX_WAIT_FOR_UNKNOWN_NOTIFICATION,
                TimeUnit.MILLISECONDS);

        if (notification == null) {
            Assertions.fail("Did not receive a notification within the timeout limit of "
                    + MAX_WAIT_FOR_UNKNOWN_NOTIFICATION + " milliseconds.");
        }

        LOGGER.info("Received notification for correlation UID {} for type {} with result {}.",
                notification.getCorrelationUid(), notification.getNotificationType(), notification.getResult());

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, notification.getCorrelationUid());

        /*
         * Organization identification and user name are always needed to
         * retrieve a response.
         */
        ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        ScenarioContext.current().put(PlatformKeys.KEY_USER_NAME, PlatformDefaults.DEFAULT_USER_NAME);

        /*
         * We did not know for which correlation UID the notification is
         * received in this implementation. In some scenarios (for instance when
         * re-establishing the RTU connection) this is because the GetData
         * request for which the notification is received was not issued from a
         * test step, in others it may be because the RTU device initiated the
         * notification without a prior request. In order to retrieve the
         * response the correlation UID might be used later-on from the scenario
         * context. This will wait for a notification for the correlation UID
         * that was stored in this method, which will no longer arrive, unless
         * the notification service is notified again, which is done in the next
         * line.
         */
        this.notificationService.handleNotification(notification, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    }

    @Then("^a notification is sent$")
    public void aNotificationIsSent(final Map<String, String> settings) {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        this.waitForNotification(MAX_WAIT_FOR_NOTIFICATION, correlationUid, true);
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent(final Map<String, String> settings) {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        this.waitForNotification(MAX_WAIT_FOR_NOTIFICATION, correlationUid, false);
    }

    private void waitForNotification(final int maxTimeOut, final String correlationUid,
            final boolean expectCorrelationUid) {

        LOGGER.info(
                "Waiting to make sure {} notification is received for correlation UID {} for at most {} milliseconds.",
                expectCorrelationUid ? "a" : "no", correlationUid, maxTimeOut);

        final Notification notification = this.notificationService.getNotification(correlationUid, maxTimeOut,
                TimeUnit.MILLISECONDS);

        final boolean gotExpectedNotification = expectCorrelationUid && notification != null;
        final boolean didNotGetUnexpectedNotification = !expectCorrelationUid && notification == null;
        if (gotExpectedNotification || didNotGetUnexpectedNotification) {
            LOGGER.info("Successfully received notification for correlation UID: {}", correlationUid);
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
