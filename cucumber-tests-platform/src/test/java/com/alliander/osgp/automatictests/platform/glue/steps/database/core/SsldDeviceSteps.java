/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.database.core;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getInteger;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
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
        final String deviceIdentification = getString(settings, Keys.DEVICE_IDENTIFICATION);
        Ssld ssld = new Ssld(deviceIdentification);

        ssld.setPublicKeyPresent(getBoolean(settings, Keys.PUBLICKEYPRESENT, Defaults.PUBLICKEYPRESENT));
        ssld.setHasSchedule(getBoolean(settings, Keys.HAS_SCHEDULE, Defaults.HASSCHEDULE));

        if (settings.containsKey(Keys.INTERNALID) || settings.containsKey(Keys.EXTERNALID)
                || settings.containsKey(Keys.RELAY_TYPE)) {
            final List<DeviceOutputSetting> dosList = new ArrayList<>();
            final int internalId = getInteger(settings, Keys.INTERNALID, Defaults.INTERNALID),
                    externalId = getInteger(settings, Keys.EXTERNALID, Defaults.EXTERNALID);
            final RelayType relayType = getEnum(settings, Keys.RELAY_TYPE, RelayType.class, RelayType.LIGHT);

            if (relayType != null) {
                dosList.add(new DeviceOutputSetting(internalId, externalId, relayType));

                ssld.updateOutputSettings(dosList);
            }
        }

        ssld = this.ssldRepository.save(ssld);

        // now update the common stuff of the SSLD device.
        this.updateDevice(deviceIdentification, settings);
        
        // Return the updated ssld device.
        return this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    }
}