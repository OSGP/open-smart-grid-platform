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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications.ResendNotificationJobSteps;

import cucumber.api.java.en.Then;

public class NotificationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationJobSteps.class);

    private static final int WAIT_FOR_NEXT_NOTIFICATION_CHECK = 1000;
    /*
     * Allow a little more time than the period for the job trigger to re-send
     * notifications.
     */
    private static final int MAX_WAIT_FOR_NOTIFICATION = 65000;

    @Autowired
    private ResponseDataRepository responseDataRespository;

    @Then("^a notification is sent$")
    public void aNotificationIsSent() throws Throwable {

        /*
         * Preferably the actual notification would be caught in the code
         * backing the test scenarios, but such a mechanism is not yet in place
         * for smart metering.
         *
         * As a workaround the correlation UID and number of notifications sent
         * for response data is stored in the scenario context when the given
         * response data is setup.
         *
         * If the actual response data has a higher number of notifications sent
         * than the value stored in the scenario context, assume a notification
         * has been sent.
         */

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Short numberOfNotificationsSent = (Short) ScenarioContext.current()
                .get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT);
        if (correlationUid == null || numberOfNotificationsSent == null) {
            fail("No " + PlatformKeys.KEY_CORRELATION_UID + " or " + PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }

        for (int delayedtime = 0; delayedtime < MAX_WAIT_FOR_NOTIFICATION; delayedtime += WAIT_FOR_NEXT_NOTIFICATION_CHECK) {
            try {
                Thread.sleep(WAIT_FOR_NEXT_NOTIFICATION_CHECK);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }
            final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
            if (responseData.getNumberOfNotificationsSent() > numberOfNotificationsSent) {
                return;
            }
        }

        fail("A notification for correlation UID " + correlationUid + " has not been sent within "
                + MAX_WAIT_FOR_NOTIFICATION + " milliseconds");
    }

    @Then("^no notification is sent$")
    public void noNotificationIsSent() throws Throwable {

        /*
         * A workaround for the actual notification is in place, see the
         * comments with aNotificationIsSent for more details.
         */

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Short numberOfNotificationsSent = (Short) ScenarioContext.current()
                .get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT);
        if (correlationUid == null || numberOfNotificationsSent == null) {
            fail("No " + PlatformKeys.KEY_CORRELATION_UID + " or " + PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT
                    + " stored in the scenario context. Unable to make assumptions as to whether a notification has been sent.");
        }

        for (int delayedtime = 0; delayedtime < MAX_WAIT_FOR_NOTIFICATION; delayedtime += WAIT_FOR_NEXT_NOTIFICATION_CHECK) {
            try {
                Thread.sleep(WAIT_FOR_NEXT_NOTIFICATION_CHECK);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }
            final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
            if (responseData.getNumberOfNotificationsSent() > numberOfNotificationsSent) {
                fail("A notification for correlation UID " + correlationUid + " was sent");
            }
        }
    }
}
