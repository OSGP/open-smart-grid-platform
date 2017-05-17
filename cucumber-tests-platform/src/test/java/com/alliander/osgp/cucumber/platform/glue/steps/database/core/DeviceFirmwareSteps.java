/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.core.Helpers.getDateTime;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;

import cucumber.api.java.en.Given;

public class DeviceFirmwareSteps extends GlueBase {

	@Autowired
	private DeviceFirmwareRepository deviceFirmwareRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareRepository firmwareRepository;

    /**
     * Generic method which adds a device firmware using the settings.
     *
     * @param settings
     *            The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device firmware$")
    public void aDeviceFirmware(final Map<String, String> settings) throws Throwable {

        // Get the device
        final Device device = this.deviceRepository.findByDeviceIdentification(
                getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        
        // Get the latest firmware
        final List<Firmware> firmwares = this.firmwareRepository.findAll();
        final Firmware firmware = firmwares.get(firmwares.size() - 1);

        final DeviceFirmware deviceFirmware = new DeviceFirmware();
		deviceFirmware.setInstalledBy(getString(settings, PlatformKeys.FIRMWARE_INSTALLED_BY, PlatformDefaults.FIRMWARE_INSTALLED_BY));
		deviceFirmware.setInstallationDate(
				getDateTime(getString(settings, PlatformKeys.FIRMWARE_INSTALLATION_DATE, PlatformDefaults.FIRMWARE_INSTALLATION_DATE))
						.toDate());
		deviceFirmware.setActive(getBoolean(settings, PlatformKeys.DEVICEFIRMWARE_ACTIVE, PlatformDefaults.DEVICE_FIRMWARE_ACTIVE));
		deviceFirmware.setDevice(device);
		deviceFirmware.setFirmware(firmware);
		
        this.deviceFirmwareRepository.save(deviceFirmware);
    }
}
