/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.DateTimeHelper.getDateTime;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmwareFile;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareFileRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareFileRepository;

import cucumber.api.java.en.Given;

public class DeviceFirmwareFileSteps extends GlueBase {

    @Autowired
    private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

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
        final Device device = this.deviceRepository.findByDeviceIdentification(getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        // Get the firmware file
        FirmwareFile firmwareFile;
        final String firmwareFileName = getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME);
        if (StringUtils.isEmpty(firmwareFileName)) {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findAll();
            firmwareFile = firmwareFiles.get(firmwareFiles.size() - 1);
        } else {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByFilename(firmwareFileName);
            firmwareFile = firmwareFiles.get(0);
        }

        final Date installationDate = getDateTime(getString(settings, PlatformKeys.FIRMWARE_INSTALLATION_DATE,
                PlatformDefaults.FIRMWARE_INSTALLATION_DATE)).toDate();
        final String installedBy = getString(settings, PlatformKeys.FIRMWARE_INSTALLED_BY,
                PlatformDefaults.FIRMWARE_INSTALLED_BY);
        final DeviceFirmwareFile deviceFirmwareFile = new DeviceFirmwareFile(device, firmwareFile, installationDate,
                installedBy);

        this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
    }
}
