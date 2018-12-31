/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.hooks.SimulatePushedAlarmsHooks;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ServiceEndpoint;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReceivedAlarmNotificationsSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivedAlarmNotificationsSteps.class);

    private static final String PATTERN = "DlmsPushNotification \\[device = \\w*, trigger type = Push alarm monitor, alarms=\\[(\\w*(, )?)+\\]\\]";

    @Autowired
    private DeviceLogItemPagingRepository deviceLogItemRepository;

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

        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final int numberOfMatchingLogs = 2;
        final Predicate<DeviceLogItem> filter = dli -> Pattern.matches(PATTERN, dli.getDecodedMessage());

        final Runnable assertion = () -> {

            final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
            final Page<DeviceLogItem> deviceLogPage = this.deviceLogItemRepository
                    .findByDeviceIdentification(deviceIdentification, pageable);
            final List<DeviceLogItem> filteredDeviceLogItems = deviceLogPage.getContent().stream().filter(filter)
                    .collect(Collectors.toList());

            assertEquals("Number of DlmsPushNotification DeviceLogItems for alarms from device " + deviceIdentification,
                    numberOfMatchingLogs, filteredDeviceLogItems.size());
        };

        final int numberOfRetries = 25;
        final long delay = 2;
        final TimeUnit unit = TimeUnit.SECONDS;
        try {
            RetryableAssert.assertWithRetries(assertion, numberOfRetries, delay, unit);
        } catch (final AssertionError e) {
            throw new AssertionError("Failed to find " + numberOfMatchingLogs
                    + " DlmsPushNotification log items for alarms from device " + deviceIdentification + " within "
                    + RetryableAssert.describeMaxDuration(numberOfRetries, delay, unit), e);
        }
    }

}
