/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core.builders;

import java.util.Date;
import java.util.Map;

import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.UtcDateHelper;
import com.alliander.osgp.cucumber.platform.inputparsers.DateInputParser;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmwareFile;
import com.alliander.osgp.domain.core.entities.FirmwareFile;

public class DeviceFirmwareFileBuilder implements CucumberBuilder<DeviceFirmwareFile> {

    private Date installationDate = UtcDateHelper.getUtcDate();
    private String installedBy = "test-org";
    private FirmwareFile firmwareFile;
    private Device device;

    public DeviceFirmwareFileBuilder withInstallationDate(final Date installationDate) {
        this.installationDate = installationDate;
        return this;
    }

    public DeviceFirmwareFileBuilder withInstalledBy(final String installedBy) {
        this.installedBy = installedBy;
        return this;
    }

    public DeviceFirmwareFileBuilder withFirmwareFile(final FirmwareFile firmwareFile) {
        this.firmwareFile = firmwareFile;
        return this;
    }

    public DeviceFirmwareFileBuilder withDevice(final Device device) {
        this.device = device;
        return this;
    }

    @Override
    public DeviceFirmwareFile build() {
        final DeviceFirmwareFile deviceFirmware = new DeviceFirmwareFile(this.device, this.firmwareFile,
                this.installationDate, this.installedBy);
        return deviceFirmware;
    }

    @Override
    public DeviceFirmwareFileBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(PlatformKeys.DEVICEFIRMWARE_INSTALLATIONDATE)) {
            this.withInstallationDate(
                    DateInputParser.parse(inputSettings.get(PlatformKeys.DEVICEFIRMWARE_INSTALLATIONDATE)));
        }

        if (inputSettings.containsKey(PlatformKeys.DEVICEFIRMWARE_INSTALLED_BY)) {
            this.withInstalledBy(inputSettings.get(PlatformKeys.DEVICEFIRMWARE_INSTALLED_BY));
        }

        return this;
    }
}
