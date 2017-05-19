/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.hooks.SimulatePushedAlarmsHooks;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ServiceEndpoint;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReceivedAlarmNotificationsSteps extends SmartMeteringStepsBase {

    private static final String XPATH_MATCHER_PUSH_NOTIFICATION = "DlmsPushNotification \\[device = \\w*, trigger type = Push alarm monitor, alarms=\\[(\\w*(, )?)+\\]\\]";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivedAlarmNotificationsSteps.class);

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^an alarm notification is received from a known device$")
    public void anAlarmNotificationIsReceivedFromAKnownDevice(final Map<String, String> settings) throws Throwable {
        try {
            final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
            SimulatePushedAlarmsHooks.simulateAlarm(deviceIdentification, new byte[] { 0x2C, 0x00, 0x00, 0x01, 0x02 },
                    this.serviceEndpoint.getAlarmNotificationsHost(), this.serviceEndpoint.getAlarmNotificationsPort());
            SimulatePushedAlarmsHooks.simulateAlarm(deviceIdentification, new byte[] { 0x2C, 0x04, 0x20, 0x00, 0x00 },
                    this.serviceEndpoint.getAlarmNotificationsHost(), this.serviceEndpoint.getAlarmNotificationsPort());
        } catch (final Exception e) {
            LOGGER.error("Error occured simulateAlarm: ", e);
        }
    }

    @When("^an alarm notification is received from an unknown device$")
    public void anAlarmNotificationIsReceivedFromAnUnknownDevice(final Map<String, String> settings) throws Throwable {
        try {
            final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
            SimulatePushedAlarmsHooks.simulateAlarm(deviceIdentification, new byte[] { 0x2C, 0x00, 0x00, 0x01, 0x02 },
                    this.serviceEndpoint.getAlarmNotificationsHost(), this.serviceEndpoint.getAlarmNotificationsPort());
            SimulatePushedAlarmsHooks.simulateAlarm(deviceIdentification, new byte[] { 0x2C, 0x04, 0x20, 0x00, 0x00 },
                    this.serviceEndpoint.getAlarmNotificationsHost(), this.serviceEndpoint.getAlarmNotificationsPort());
        } catch (final Exception e) {
            LOGGER.error("Error occured simulating an alarm", e);
        }
    }

    @Then("^the alarm should be pushed to the osgp_logging database device_log_item table$")
    public void theAlarmShouldBePushedToTheOsgpLoggingDatabaseTable(final Map<String, String> settings)
            throws Throwable {
        final Pattern responsePattern = Pattern.compile(XPATH_MATCHER_PUSH_NOTIFICATION);

        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final List<DeviceLogItem> deviceLogItems = this.findDeviceLogItems(deviceIdentification, 2);
        if (null == deviceLogItems) {
            Assert.fail("DeviceLogItems were not found in the database");
        }

        for (int i = 0; i < 2; i++) {
            final DeviceLogItem item = deviceLogItems.get(i);
            LOGGER.info("CreationTime: {}", item.getCreationTime().toString());
            LOGGER.info("DecodedMessage: {}", item.getDecodedMessage());

            final Matcher responseMatcher = responsePattern.matcher(item.getDecodedMessage());
            assertTrue("a matching DlmsPushNotification is logged.", responseMatcher.find());
        }
    }

    /*
     * it may take some time before the records are to the dev_log_item table,
     * so we have to poll.
     */
    private List<DeviceLogItem> findDeviceLogItems(final String deviceIdentification, final int minExcepted) {
        int loopCount = 0;
        while (loopCount++ < 25) {
            this.sleep(2000L);
            final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                    .findByDeviceIdentification(deviceIdentification, new PageRequest(0, 2)).getContent();
            if (deviceLogItems != null && deviceLogItems.size() >= minExcepted) {
                return deviceLogItems;
            }
        }
        return null;
    }

    private void sleep(final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep was interrupted", e);
        }
    }
}
