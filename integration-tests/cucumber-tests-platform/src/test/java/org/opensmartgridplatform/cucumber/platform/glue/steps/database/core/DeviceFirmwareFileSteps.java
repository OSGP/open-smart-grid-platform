/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceFirmwareFileSteps {

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
        final FirmwareFile firmwareFile = this
                .getFirmwareFile(getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME));

        final Date installationDate = getDateTime(getString(settings, PlatformKeys.FIRMWARE_INSTALLATION_DATE,
                PlatformDefaults.FIRMWARE_INSTALLATION_DATE)).toDate();
        final String installedBy = getString(settings, PlatformKeys.FIRMWARE_INSTALLED_BY,
                PlatformDefaults.FIRMWARE_INSTALLED_BY);
        final DeviceFirmwareFile deviceFirmwareFile = new DeviceFirmwareFile(device, firmwareFile, installationDate,
                installedBy);

        this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
    }

    private FirmwareFile getFirmwareFile(final String firmwareFileName) {
        FirmwareFile firmwareFile;
        if (StringUtils.isEmpty(firmwareFileName)) {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findAll();
            firmwareFile = firmwareFiles.get(firmwareFiles.size() - 1);
        } else {
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findByFilename(firmwareFileName);
            firmwareFile = firmwareFiles.get(0);
        }
        return firmwareFile;
    }

    @Then("^the device firmware file exists$")
    public void theDeviceFirmwareFileExists(final Map<String, String> settings) throws Throwable {
        final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final FirmwareFile firmwareFile = this
                .getFirmwareFile(getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME));
        final DeviceFirmwareFile deviceFirmwareFile = Wait.untilAndReturn(() -> {
            final DeviceFirmwareFile entity = this.deviceFirmwareFileRepository.findByDeviceAndFirmwareFile(device,
                    firmwareFile);
            if (entity == null) {
                throw new Exception("Device with identification [" + deviceIdentification + "]");
            }

            return entity;
        });

        assertThat(deviceFirmwareFile.getDevice().getDeviceIdentification()).isEqualTo(deviceIdentification);
    }
}
