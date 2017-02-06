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

import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.helpers.UtcDateHelper;
import com.alliander.osgp.cucumber.platform.inputparsers.DateInputParser;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.Firmware;

public class DeviceFirmwareBuilder implements CucumberBuilder<DeviceFirmware> {

    private Date installationDate = UtcDateHelper.getUtcDate();
    private String installedBy = "test-org";
    private boolean active = true;
    private Firmware firmware;
    private Device device;

    public DeviceFirmwareBuilder withInstallationDate(final Date installationDate) {
        this.installationDate = installationDate;
        return this;
    }

    public DeviceFirmwareBuilder withInstalledBy(final String installedBy) {
        this.installedBy = installedBy;
        return this;
    }

    public DeviceFirmwareBuilder withActive(final boolean active) {
        this.active = active;
        return this;
    }

    public DeviceFirmwareBuilder withFirmware(final Firmware firmware) {
        this.firmware = firmware;
        return this;
    }

    public DeviceFirmwareBuilder withDevice(final Device device) {
        this.device = device;
        return this;
    }

    @Override
    public DeviceFirmware build() {
        final DeviceFirmware deviceFirmware = new DeviceFirmware();
        deviceFirmware.setInstallationDate(this.installationDate);
        deviceFirmware.setInstalledBy(this.installedBy);
        deviceFirmware.setActive(this.active);
        deviceFirmware.setFirmware(this.firmware);
        deviceFirmware.setDevice(this.device);
        return deviceFirmware;
    }

    @Override
    public DeviceFirmwareBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.DEVICEFIRMWARE_INSTALLATIONDATE)) {
            this.withInstallationDate(DateInputParser.parse(inputSettings.get(Keys.DEVICEFIRMWARE_INSTALLATIONDATE)));
        }

        if (inputSettings.containsKey(Keys.DEVICEFIRMWARE_INSTALLED_BY)) {
            this.withInstalledBy(inputSettings.get(Keys.DEVICEFIRMWARE_INSTALLED_BY));
        }

        if (inputSettings.containsKey(Keys.DEVICEFIRMWARE_ACTIVE)) {
            this.withActive(Boolean.parseBoolean(inputSettings.get(Keys.DEVICEFIRMWARE_ACTIVE)));
        }

        return this;
    }
}
