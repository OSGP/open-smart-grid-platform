/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.ws.microgrids.notification;

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.NotificationService;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class NotificationSteps extends GlueBase {

    private static final int MAX_WAIT_FOR_NOTIFICATION = 1200000;
    private static final int MAX_WAIT_FOR_RESEND_NOTIFICATION = 65000;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSteps.class);

    @Autowired
    private NotificationService notificationService;

    @When("^the OSGP connection is lost with the RTU device$")
    public void theOSGPConnectionIsLostWithTheRTUDevice() throws Throwable {

    }

    @Then("^I should receive a notification$")
    public void iShouldReceiveANotification() throws Throwable {
        LOGGER.info("Waiting for a notification for at most {} milliseconds.", MAX_WAIT_FOR_NOTIFICATION);

        final Notification notification = this.notificationService.getNotification(MAX_WAIT_FOR_NOTIFICATION,
                TimeUnit.MILLISECONDS);

        if (notification == null) {
            fail("Did not receive a notification within the timeout limit of " + MAX_WAIT_FOR_NOTIFICATION
                    + " milliseconds.");
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
    }

    @Then("^a notification is sent$")
    public void aNotificationIsSent(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        this.waitForNotification(MAX_WAIT_FOR_RESEND_NOTIFICATION, correlationUid, true);
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        this.waitForNotification(MAX_WAIT_FOR_RESEND_NOTIFICATION, correlationUid, false);
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
            fail("Did not receive a notification for correlation UID: " + correlationUid + " within " + maxTimeOut
                    + " milliseconds");
        } else {
            fail("Received notification for correlation UID: " + correlationUid);
        }
    }
}
