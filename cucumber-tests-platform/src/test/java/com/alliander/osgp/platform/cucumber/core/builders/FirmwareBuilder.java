/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.core.builders;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class FirmwareBuilder implements CucumberBuilder<Firmware> {

    private DeviceModel deviceModel;
    private String filename;
    private String description;
    private boolean pushToNewDevices;
    private String moduleVersionComm;
    private String moduleVersionFunc;
    private String moduleVersionMa;
    private String moduleVersionMbus;
    private String moduleVersionSec;
    private byte file[];
    private String hash;

    public FirmwareBuilder withDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public FirmwareBuilder withFilename(final String filename) {
        this.filename = filename;
        return this;
    }

    public FirmwareBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public FirmwareBuilder withPushToNewDevices(final boolean pushToNewDevices) {
        this.pushToNewDevices = pushToNewDevices;
        return this;
    }

    public FirmwareBuilder withModuleVersionComm(final String moduleVersionComm) {
        this.moduleVersionComm = moduleVersionComm;
        return this;
    }

    public FirmwareBuilder withModuleVersionFunc(final String moduleVersionFunc) {
        this.moduleVersionFunc = moduleVersionFunc;
        return this;
    }

    public FirmwareBuilder withModuleVersionMa(final String moduleVersionMa) {
        this.moduleVersionMa = moduleVersionMa;
        return this;
    }

    public FirmwareBuilder withModuleVersionMbus(final String moduleVersionMbus) {
        this.moduleVersionMbus = moduleVersionMbus;
        return this;
    }

    public FirmwareBuilder withModuleVersionSec(final String moduleVersionSec) {
        this.moduleVersionSec = moduleVersionSec;
        return this;
    }

    public FirmwareBuilder withFile(final byte[] file) {
        this.file = file;
        return this;
    }

    public FirmwareBuilder withHash(final String hash) {
        this.hash = hash;
        return this;
    }

    @Override
    public Firmware build() {
        final Firmware firmware = new Firmware();
        firmware.setDeviceModel(this.deviceModel);
        firmware.setFilename(this.filename);
        firmware.setDescription(this.description);
        firmware.setPushToNewDevices(this.pushToNewDevices);
        firmware.setModuleVersionComm(this.moduleVersionComm);
        firmware.setModuleVersionFunc(this.moduleVersionFunc);
        firmware.setModuleVersionMa(this.moduleVersionMa);
        firmware.setModuleVersionMbus(this.moduleVersionMbus);
        firmware.setModuleVersionSec(this.moduleVersionSec);
        firmware.setFile(this.file);
        firmware.setHash(this.hash);
        return firmware;
    }

    @Override
    public FirmwareBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.FIRMWARE_FILENAME)) {
            this.withFilename(inputSettings.get(Keys.FIRMWARE_FILENAME));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_DESCRIPTION)) {
            this.withDescription(inputSettings.get(Keys.FIRMWARE_DESCRIPTION));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_PUSH_TO_NEW_DEVICES)) {
            this.withPushToNewDevices(Boolean.parseBoolean(inputSettings.get(Keys.FIRMWARE_PUSH_TO_NEW_DEVICES)));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_MODULE_VERSION_COMM)) {
            this.withModuleVersionComm(inputSettings.get(Keys.FIRMWARE_MODULE_VERSION_COMM));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_MODULE_VERSION_FUNC)) {
            this.withModuleVersionFunc(inputSettings.get(Keys.FIRMWARE_MODULE_VERSION_FUNC));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_MODULE_VERSION_MA)) {
            this.withModuleVersionMa(inputSettings.get(Keys.FIRMWARE_MODULE_VERSION_MA));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_MODULE_VERSION_MBUS)) {
            this.withModuleVersionMbus(inputSettings.get(Keys.FIRMWARE_MODULE_VERSION_MBUS));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_MODULE_VERSION_SEC)) {
            this.withModuleVersionSec(inputSettings.get(Keys.FIRMWARE_MODULE_VERSION_SEC));
        }

        if (inputSettings.containsKey(Keys.FIRMWARE_HASH)) {
            this.withHash(inputSettings.get(Keys.FIRMWARE_HASH));
        }

        return this;
    }

}
