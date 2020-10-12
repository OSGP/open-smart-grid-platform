/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.config.Iec60870MockServerConfig;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.RtuDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.cucumber.java.en.Given;

/**
 * IEC 60870 device specific steps.
 */
public class Iec60870DeviceSteps {

    private static final String DEFAULT_DEVICE_TYPE = "RTU";
    private static final String DEFAULT_PROTOCOL = "60870-5-104";
    private static final String DEFAULT_PROTOCOL_VERSION = "1.0";

    private static final Map<String, String> RTU_60870_DEFAULT_SETTINGS;

    static {
        final Map<String, String> settingsMap = new HashMap<>();
        settingsMap.put(PlatformKeys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
        settingsMap.put(PlatformKeys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
        settingsMap.put(PlatformKeys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
        settingsMap.put(PlatformDistributionAutomationKeys.PROFILE, PlatformDistributionAutomationDefaults.PROFILE);

        RTU_60870_DEFAULT_SETTINGS = Collections.unmodifiableMap(settingsMap);
    }

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private RtuDeviceSteps rtuDeviceSteps;

    @Autowired
    private Iec60870MockServerConfig mockServerConfig;

    /**
     * Creates an IEC 60870 RTU.
     */
    @Given("^an IEC 60870 RTU$")
    public void anIec60870Rtu(final Map<String, String> settings) {

        ScenarioContext.current()
                .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
        final Map<String, String> rtuSettings = SettingsHelper.addAsDefaults(settings, RTU_60870_DEFAULT_SETTINGS);
        ScenarioContext.current()
                .put(PlatformDistributionAutomationKeys.PROFILE,
                        rtuSettings.get(PlatformDistributionAutomationKeys.PROFILE));

        rtuSettings.put(PlatformKeys.KEY_NETWORKADDRESS, this.mockServerConfig.iec60870MockNetworkAddress());

        this.rtuDeviceSteps.anRtuDevice(rtuSettings);

        this.rtuDeviceSteps.updateRtuDevice(rtuSettings);

        this.createIec60870Device(rtuSettings);
    }

    @Transactional("txMgrIec60870")
    private void createIec60870Device(final Map<String, String> settings) {

        final Iec60870Device iec60870Device = new Iec60870Device(getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        iec60870Device.setCommonAddress(getInteger(settings, PlatformDistributionAutomationKeys.COMMON_ADDRESS,
                PlatformDistributionAutomationDefaults.COMMON_ADDRESS));
        iec60870Device.setPort(getInteger(settings, PlatformDistributionAutomationKeys.PORT,
                PlatformDistributionAutomationDefaults.PORT));

        this.iec60870DeviceRepository.save(iec60870Device);
    }
}
