/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.hooks.SimulatePushedAlarmsHooks;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ServiceEndpoint;

import cucumber.api.java.en.When;

public class PushNotificationAlarm {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationAlarm.class);

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^an alarm is received from a known device$")
    public void anAlarmIsReceivedFromAKnownDevice(final Map<String, String> settings) throws Throwable {
        try {
            final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
            SimulatePushedAlarmsHooks.simulateAlarm(deviceIdentification, new byte[] { 0x2C, 0x00, 0x00, 0x01, 0x02 },
                    this.serviceEndpoint.getAlarmNotificationsHost(), this.serviceEndpoint.getAlarmNotificationsPort());
        } catch (final Exception e) {
            LOGGER.error("Error occured simulateAlarm: ", e);
        }

    }

}
