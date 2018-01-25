/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications.ResendNotificationsSteps;

import cucumber.api.java.en.Then;

public class NotificationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationsSteps.class);

    private final int TIMEOUT = 30000;

    @Then("^a notification is sent$")
    public void aNotificationIsSent() throws Throwable {
        // To be implemented when notification service mock is implemented for this
        // value stream
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent() throws Throwable {
        // To be implemented when notification service mock is implemented for this
        // value stream
        // needed to make sure the final ResendNotificationJob has at final least runned
        // once
        try {
            Thread.sleep(this.TIMEOUT);
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }

}
