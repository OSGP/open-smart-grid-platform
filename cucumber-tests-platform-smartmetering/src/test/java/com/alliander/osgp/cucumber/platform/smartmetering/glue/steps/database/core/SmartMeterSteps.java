/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.cucumber.core.Helpers.getShort;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;

import cucumber.api.java.en.Given;

public class SmartMeterSteps extends BaseDeviceSteps {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    /**
     * Given a smart meter exists.
     *
     * @param settings
     */
    @Given("^a smart meter$")
    @Transactional("txMgrCore")
    public Device aSmartMeter(final Map<String, String> settings) {

        final String deviceIdentification = getString(settings, "DeviceIdentification",
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
        final SmartMeter smartMeter = new SmartMeter(deviceIdentification,
                getString(settings, "Alias", PlatformDefaults.DEFAULT_ALIAS),
                getString(settings, "ContainerCity", PlatformDefaults.DEFAULT_CONTAINER_CITY),
                getString(settings, "ContainerPostalCode", PlatformDefaults.DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, "ContainerStreet", PlatformDefaults.DEFAULT_CONTAINER_STREET),
                getString(settings, "ContainerNumber", PlatformDefaults.DEFAULT_CONTAINER_NUMBER),
                getString(settings, "ContainerMunicipality", PlatformDefaults.DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, "GPSLatitude", PlatformDefaults.DEFAULT_LATITUDE),
                getFloat(settings, "GPSLongitude", PlatformDefaults.DEFAULT_LONGITUDE));

        smartMeter.setSupplier(getString(settings, PlatformKeys.SUPPLIER, PlatformDefaults.DEFAULT_SUPPLIER));

        if (settings.containsKey(PlatformKeys.KEY_GATEWAY_DEVICE_ID)) {
            smartMeter.setChannel(getShort(settings, PlatformKeys.KEY_CHANNEL, PlatformDefaults.DEFAULT_CHANNEL));
            final Device smartEMeter = this.deviceRepository
                    .findByDeviceIdentification(settings.get(PlatformKeys.KEY_GATEWAY_DEVICE_ID));
            smartMeter.updateGatewayDevice(smartEMeter);
        }

        this.smartMeterRepository.save(smartMeter);

        return this.updateDevice(deviceIdentification, settings);
    }
}
