package com.alliander.osgp.platform.cucumber.steps.ws.microgrids.notification;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.notification.MockNotificationService;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.microgrids.MicrogridsStepsBase;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class NotificationSteps extends MicrogridsStepsBase {

    private int WAIT_FOR_NEXT_NOTIFICATION_CHECK = 1000;
    private int MAX_WAIT_FOR_NOTIFICATION = 1200000;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSteps.class);

    @Autowired
    MockNotificationService mockNotificationService;

    @When("^the OSGP connection is lost with the RTU device$")
    public void theOSGPConnectionIsLostWithTheRTUDevice() throws Throwable {

    }

    @Then("^I receive a notification$")
    public void iReceiveANotification() throws Throwable {
        int waited = 0;

        while (!this.mockNotificationService.receivedNotification() && waited < this.MAX_WAIT_FOR_NOTIFICATION) {
            LOGGER.info("Checking and waiting for notification.");
            Thread.sleep(this.WAIT_FOR_NEXT_NOTIFICATION_CHECK);
            waited += this.WAIT_FOR_NEXT_NOTIFICATION_CHECK;
        }

        final Notification notification = this.mockNotificationService.getNotification();
        if (notification != null) {
            ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, notification.getCorrelationUid());

            // Organisation Identification is always needed to retrieve a
            // response.
            if (!ScenarioContext.Current().containsKey(Keys.KEY_ORGANISATION_IDENTIFICATION)) {
                ScenarioContext.Current().put(Keys.KEY_ORGANISATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANISATION_IDENTIFICATION);
            }

            // Username is always needed to retrieve a
            // response.
            if (!ScenarioContext.Current().containsKey(Keys.KEY_USER_NAME)) {
                ScenarioContext.Current().put(Keys.KEY_USER_NAME, Defaults.DEFAULT_USER_NAME);
            }
        } else {
            Assert.fail("Did not receive a notification within the timeout limit.");
        }
    }
}