/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.ws.microgrids.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.When;

public class ResendNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationsSteps.class);

    private final int INITIAL_TIMEOUT = 60000;

    @When("^OSGP checks for which response data a notification has to be resend$")
    public void osgpChecksForWhichResponseDataANotificationHasToBeResend() throws Throwable {
        // needed to make sure the ResendNotificationJob has at least runned once
        try {
            Thread.sleep(this.INITIAL_TIMEOUT);
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }
}
