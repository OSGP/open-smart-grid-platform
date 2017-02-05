/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.database.adapterprotocolie61850;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.StepsBase;
import com.alliander.osgp.platform.cucumber.config.Iec61850MockServerConfig;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.glue.steps.database.core.RtuDeviceSteps;
import com.alliander.osgp.platform.cucumber.helpers.SettingsHelper;

import cucumber.api.java.en.Given;

/**
 * IEC61850 specific device steps.
 */
public class Iec61850DeviceSteps extends StepsBase {

    private static final String DEFAULT_DEVICE_TYPE = "RTU";
    private static final String DEFAULT_PROTOCOL = "IEC61850";
    private static final String DEFAULT_PROTOCOL_VERSION = "1.0";

    private static final Map<String, String> RTU_DEFAULT_SETTINGS = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    this.put(Keys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
                    this.put(Keys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
                    this.put(Keys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
                }
            });

    @Autowired
    private Iec61850MockServerConfig iec61850MockServerConfig;

    @Autowired
    private Iec61850DeviceRepository iec61850DeviceRespository;

    @Autowired
    private RtuDeviceSteps rtuDeviceSteps;

    /**
     * Creates an IEC61850 device.
     * 
     * @param settings
     * @throws Throwable
     */
    @Given("^an rtu iec61850 device$")
    public void anRtuIec61850Device(final Map<String, String> settings) throws Throwable {

        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION);
        final Map<String, String> rtuSettings = SettingsHelper.addAsDefaults(settings, RTU_DEFAULT_SETTINGS);
        rtuSettings.put(Keys.KEY_NETWORKADDRESS, this.iec61850MockServerConfig.iec61850MockNetworkAddress());
        rtuSettings.put(Keys.KEY_NETWORKADDRESS, this.iec61850MockServerConfig.iec61850MockNetworkAddress());
        this.rtuDeviceSteps.anRtuDevice(rtuSettings);

        createIec61850Device(rtuSettings);
    }

    @Transactional("txMgrIec61850")
    private void createIec61850Device(final Map<String, String> settings) {

        /*
         * Make sure an ICD filename and port corresponding to the mock server
         * settings will be used from the application to connect to the device.
         */
        final Iec61850Device iec61850Device = new Iec61850Device(
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));
        iec61850Device.setIcdFilename(this.iec61850MockServerConfig.iec61850MockIcdFilename());
        iec61850Device.setPort(this.iec61850MockServerConfig.iec61850MockPort());

        this.iec61850DeviceRespository.save(iec61850Device);
    }
}
