/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.RelayType;

import cucumber.api.java.en.Given;

public class SsldDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private SsldRepository ssldRepository;

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