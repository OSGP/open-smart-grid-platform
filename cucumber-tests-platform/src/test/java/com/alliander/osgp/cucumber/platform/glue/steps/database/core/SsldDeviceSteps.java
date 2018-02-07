/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.DateTimeHelper.getDateTime2;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getBoolean;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getInteger;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.RelayStatusRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.RelayType;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SsldDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private RelayStatusRepository relayStatusRepository;

    @Autowired
    private DeviceSteps deviceSteps;

    /**
     * Creates a new device.
     *
     * @param settings
     * @return
     * @throws Throwable
     * @deprecated Deprecated because you should specify the type in a test.
     *             SSLD/RTU or a different one. This method now will create a
     *             SSLD device.
     */
    @Deprecated
    @Given("^a device$")
    @Transactional("txMgrCore")
    public Ssld aDevice(final Map<String, String> settings) throws Throwable {
        return this.createAnSsldDevice(settings);
    }

    /**
     * Generic method which adds an SSLD device using the settings.
     *
     * @param settings
     *            The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^an ssld device$")
    @Transactional("txMgrCore")
    public Ssld anSsldDevice(final Map<String, String> settings) throws Throwable {
        return this.createAnSsldDevice(settings);
    }

    @Given("^a relay status$")
    @Transactional("txMgrCore")
    public void aRelayStatus(final Map<String, String> settings) throws Exception {

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final String[] deviceOutputSettings = getString(settings, PlatformKeys.DEVICE_OUTPUT_SETTINGS,
                PlatformDefaults.DEVICE_OUTPUT_SETTINGS).replaceAll(" ", "").split(PlatformKeys.SEPARATOR_SEMICOLON),
                relayStatuses = getString(settings, PlatformKeys.RELAY_STATUSES, PlatformDefaults.RELAY_STATUSES)
                        .replaceAll(" ", "").split(PlatformKeys.SEPARATOR_SEMICOLON);

        final List<DeviceOutputSetting> dosList = new ArrayList<>();

        for (final String dos : deviceOutputSettings) {
            final String[] deviceOutputSetting = dos.split(PlatformKeys.SEPARATOR_COMMA);
            dosList.add(new DeviceOutputSetting(Integer.parseInt(deviceOutputSetting[0]),
                    Integer.parseInt(deviceOutputSetting[1]), RelayType.valueOf(deviceOutputSetting[2]),
                    deviceOutputSetting[3]));
        }

        ssld.updateOutputSettings(dosList);

        for (final String rs : relayStatuses) {
            final String[] relayStatus = rs.split(PlatformKeys.SEPARATOR_COMMA);
            this.relayStatusRepository.save(new RelayStatus(ssld, Integer.parseInt(relayStatus[0]),
                    Boolean.parseBoolean(relayStatus[1]), getDateTime2(relayStatus[2], DateTime.now()).toDate()));
        }
    }

    @Then("^theSsldDeviceContains$")
    public void theSsldDeviceContains(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            final Ssld ssld = this.ssldRepository
                    .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            Assert.assertEquals(getBoolean(expectedEntity, PlatformKeys.KEY_HAS_SCHEDULE), ssld.getHasSchedule());
        });

        this.deviceSteps.theDeviceContains(expectedEntity);
    }

    private Ssld createAnSsldDevice(final Map<String, String> settings) throws Throwable {
        // Set the required stuff
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        Ssld ssld = new Ssld(deviceIdentification);

        ssld.setPublicKeyPresent(
                getBoolean(settings, PlatformKeys.KEY_PUBLICKEYPRESENT, PlatformDefaults.DEFAULT_PUBLICKEYPRESENT));
        ssld.setHasSchedule(getBoolean(settings, PlatformKeys.KEY_HAS_SCHEDULE, PlatformDefaults.DEFAULT_HASSCHEDULE));

        if (settings.containsKey(PlatformKeys.KEY_INTERNALID) || settings.containsKey(PlatformKeys.KEY_EXTERNALID)
                || settings.containsKey(PlatformKeys.KEY_RELAY_TYPE)) {
            final List<DeviceOutputSetting> dosList = new ArrayList<>();
            final int internalId = getInteger(settings, PlatformKeys.KEY_INTERNALID,
                    PlatformDefaults.DEFAULT_INTERNALID),
                    externalId = getInteger(settings, PlatformKeys.KEY_EXTERNALID, PlatformDefaults.DEFAULT_EXTERNALID);
            final RelayType relayType = getEnum(settings, PlatformKeys.KEY_RELAY_TYPE, RelayType.class,
                    RelayType.LIGHT);

            if (relayType != null) {
                dosList.add(new DeviceOutputSetting(internalId, externalId, relayType));

                ssld.updateOutputSettings(dosList);
            }
        }

        ssld.updateInMaintenance(
                getBoolean(settings, PlatformKeys.DEVICE_IN_MAINTENANCE, PlatformDefaults.DEVICE_IN_MAINTENANCE));

        ssld = this.ssldRepository.save(ssld);

        // now update the common stuff of the SSLD device.
        this.updateDevice(deviceIdentification, settings);

        // Return the updated ssld device.
        return this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    }
}
