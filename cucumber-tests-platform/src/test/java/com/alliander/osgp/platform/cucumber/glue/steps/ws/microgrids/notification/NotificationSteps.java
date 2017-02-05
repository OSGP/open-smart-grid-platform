/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.ws.microgrids.notification;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.StepsBase;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.NotificationService;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class NotificationSteps extends StepsBase {

    private int WAIT_FOR_NEXT_NOTIFICATION_CHECK = 1000;
    private int MAX_WAIT_FOR_NOTIFICATION = 1200000;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSteps.class);

    @Autowired
    private NotificationService mockNotificationService;

    @When("^the OSGP connection is lost with the RTU device$")
    public void theOSGPConnectionIsLostWithTheRTUDevice() throws Throwable {

    }

    @Then("^I should receive a notification$")
    public void iShouldReceiveANotification() throws Throwable {
        int waited = 0;

        while (!this.mockNotificationService.receivedNotification() && waited < this.MAX_WAIT_FOR_NOTIFICATION) {
            LOGGER.info("Checking and waiting for notification.");
            Thread.sleep(this.WAIT_FOR_NEXT_NOTIFICATION_CHECK);
            waited += this.WAIT_FOR_NEXT_NOTIFICATION_CHECK;
        }

        final Notification notification = this.mockNotificationService.getNotification();
        if (notification != null) {
            ScenarioContext.Current().put(Keys.CORRELATION_UID, notification.getCorrelationUid());

            // Organisation Identification is always needed to retrieve a
            // response.
            ScenarioContext.Current().put(Keys.ORGANIZATION_IDENTIFICATION,
                    Defaults.ORGANIZATION_IDENTIFICATION);

            // Username is always needed to retrieve a response.
            ScenarioContext.Current().put(Keys.USER_NAME, Defaults.USER_NAME);
        } else {
            Assert.fail("Did not receive a notification within the timeout limit.");
        }
    }
}