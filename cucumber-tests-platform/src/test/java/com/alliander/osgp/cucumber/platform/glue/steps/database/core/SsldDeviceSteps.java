/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getDateTime2;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.wait.Wait;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.RelayStatusRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.RelayType;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SsldDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private DeviceRepository deviceRepository;

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

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        final String[] deviceOutputSettings = getString(settings, Keys.DEVICE_OUTPUT_SETTINGS,
                Defaults.DEVICE_OUTPUT_SETTINGS).replaceAll(" ", "").split(Keys.SEPARATOR_SEMICOLON),
                relayStatuses = getString(settings, Keys.RELAY_STATUSES, Defaults.RELAY_STATUSES).replaceAll(" ", "")
                        .split(Keys.SEPARATOR_SEMICOLON);

        final List<DeviceOutputSetting> dosList = new ArrayList<>();

        for (final String dos : deviceOutputSettings) {
            final String[] deviceOutputSetting = dos.split(Keys.SEPARATOR_COMMA);
            dosList.add(new DeviceOutputSetting(Integer.parseInt(deviceOutputSetting[0]),
                    Integer.parseInt(deviceOutputSetting[1]), RelayType.valueOf(deviceOutputSetting[2]),
                    deviceOutputSetting[3]));
        }

        ssld.updateOutputSettings(dosList);

        for (final String rs : relayStatuses) {
            final String[] relayStatus = rs.split(Keys.SEPARATOR_COMMA);
            this.relayStatusRepository.save(new RelayStatus(ssld, Integer.parseInt(relayStatus[0]),
                    Boolean.parseBoolean(relayStatus[1]), getDateTime2(relayStatus[2], DateTime.now()).toDate()));
        }
    }

    @Then("^theSsldDeviceContains$")
    public void theSsldDeviceContains(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            final Ssld ssld = this.ssldRepository
                    .findByDeviceIdentification(getString(expectedEntity, Keys.KEY_DEVICE_IDENTIFICATION));

            Assert.assertEquals(getBoolean(expectedEntity, Keys.KEY_HAS_SCHEDULE), ssld.getHasSchedule());
            // Assert.assertEquals(getBoolean(expectedEntity,
            // Keys.KEY_PUBLICKEYPRESENT), ssld.isPublicKeyPresent());
        });

        this.deviceSteps.theDeviceContains(expectedEntity);
    }

    private Ssld createAnSsldDevice(final Map<String, String> settings) throws Throwable {
        // Set the required stuff
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION);
        Ssld ssld = new Ssld(deviceIdentification);

        ssld.setPublicKeyPresent(getBoolean(settings, Keys.KEY_PUBLICKEYPRESENT, Defaults.DEFAULT_PUBLICKEYPRESENT));
        ssld.setHasSchedule(getBoolean(settings, Keys.KEY_HAS_SCHEDULE, Defaults.DEFAULT_HASSCHEDULE));

        if (settings.containsKey(Keys.KEY_INTERNALID) || settings.containsKey(Keys.KEY_EXTERNALID)
                || settings.containsKey(Keys.KEY_RELAY_TYPE)) {
            final List<DeviceOutputSetting> dosList = new ArrayList<>();
            final int internalId = getInteger(settings, Keys.KEY_INTERNALID, Defaults.DEFAULT_INTERNALID),
                    externalId = getInteger(settings, Keys.KEY_EXTERNALID, Defaults.DEFAULT_EXTERNALID);
            final RelayType relayType = getEnum(settings, Keys.KEY_RELAY_TYPE, RelayType.class, RelayType.LIGHT);

            if (relayType != null) {
                dosList.add(new DeviceOutputSetting(internalId, externalId, relayType));

                ssld.updateOutputSettings(dosList);
            }
        }

        ssld.updateInMaintenance(getBoolean(settings, Keys.DEVICE_IN_MAINTENANCE, Defaults.DEVICE_IN_MAINTENANCE));

        ssld = this.ssldRepository.save(ssld);

        // now update the common stuff of the SSLD device.
        this.updateDevice(deviceIdentification, settings);

        // Return the updated ssld device.
        return this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    }
}
