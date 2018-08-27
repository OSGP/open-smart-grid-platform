/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime2;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.RelayStatusRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
     * @deprecated Deprecated because you should specify the type in a test.
     *             SSLD/RTU or a different one. This method now will create a
     *             SSLD device.
     */
    @Deprecated
    @Given("^a device$")
    @Transactional("txMgrCore")
    public Ssld aDevice(final Map<String, String> settings) {
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
    public Ssld anSsldDevice(final Map<String, String> settings) {
        return this.createAnSsldDevice(settings);
    }

    @Given("^a relay status$")
    @Transactional("txMgrCore")
    public void aRelayStatus(final Map<String, String> settings) {

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
            final int index = Integer.parseInt(relayStatus[0]);
            final boolean lastKnownState = Boolean.parseBoolean(relayStatus[1]);
            final Date lastKnownSwitchingTime = getDateTime2(relayStatus[2], DateTime.now()).toDate();

            final RelayStatus currentRelayStatus = ssld.getRelayStatusByIndex(index);
            if (currentRelayStatus == null) {
                this.relayStatusRepository.save(new RelayStatus(ssld, index, lastKnownState, lastKnownSwitchingTime));
            } else {
                currentRelayStatus.setLastKnownState(lastKnownState);
                currentRelayStatus.setLastKnowSwitchingTime(lastKnownSwitchingTime);
                this.relayStatusRepository.save(currentRelayStatus);
            }
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

    private Ssld createAnSsldDevice(final Map<String, String> settings) {
        // Set the required stuff
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final Ssld ssld = new Ssld(deviceIdentification);

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

        this.ssldRepository.save(ssld);

        // now update the common stuff of the SSLD device.
        this.updateDevice(deviceIdentification, settings);

        // Return the updated ssld device.
        return this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    }
}
