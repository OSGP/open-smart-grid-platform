/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core.builders;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.repositories.FirmwareModuleRepository;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;

public class FirmwareFileBuilder implements CucumberBuilder<FirmwareFile> {

    private DeviceModel deviceModel;
    private String filename;
    private String description = PlatformDefaults.FIRMWARE_DESCRIPTION;
    private boolean pushToNewDevices = PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE;
    private String moduleVersionComm = PlatformDefaults.FIRMWARE_MODULE_VERSION_COMM;
    private String moduleVersionFunc = PlatformDefaults.FIRMWARE_MODULE_VERSION_FUNC;
    private String moduleVersionMa = PlatformDefaults.FIRMWARE_MODULE_VERSION_MA;
    private String moduleVersionMbus = PlatformDefaults.FIRMWARE_MODULE_VERSION_MBUS;
    private String moduleVersionSec = PlatformDefaults.FIRMWARE_MODULE_VERSION_SEC;
    private byte file[];
    private String hash;

    public FirmwareFileBuilder withDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public FirmwareFileBuilder withFilename(final String filename) {
        this.filename = filename;
        return this;
    }

    public FirmwareFileBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public FirmwareFileBuilder withPushToNewDevices(final boolean pushToNewDevices) {
        this.pushToNewDevices = pushToNewDevices;
        return this;
    }

    public FirmwareFileBuilder withModuleVersionComm(final String moduleVersionComm) {
        this.moduleVersionComm = moduleVersionComm;
        return this;
    }

    public FirmwareFileBuilder withModuleVersionFunc(final String moduleVersionFunc) {
        this.moduleVersionFunc = moduleVersionFunc;
        return this;
    }

    public FirmwareFileBuilder withModuleVersionMa(final String moduleVersionMa) {
        this.moduleVersionMa = moduleVersionMa;
        return this;
    }

    public FirmwareFileBuilder withModuleVersionMbus(final String moduleVersionMbus) {
        this.moduleVersionMbus = moduleVersionMbus;
        return this;
    }

    public FirmwareFileBuilder withModuleVersionSec(final String moduleVersionSec) {
        this.moduleVersionSec = moduleVersionSec;
        return this;
    }

    public FirmwareFileBuilder withFile(final byte[] file) {
        this.file = file;
        return this;
    }

    public FirmwareFileBuilder withHash(final String hash) {
        this.hash = hash;
        return this;
    }

    @Override
    public FirmwareFile build() {
        throw new UnsupportedOperationException(
                "Firmware module version configuration model in test builders needs to be made more generic. For now call: build(firmwareModuleRepository, isForSmartMeters)");
    }

    public FirmwareFile build(final FirmwareModuleRepository firmwareModuleRepository, final boolean isForSmartMeters) {
        final FirmwareFile firmwareFile = new FirmwareFile();
        if (this.deviceModel != null) {
            firmwareFile.addDeviceModel(this.deviceModel);
        }
        firmwareFile.setFilename(this.filename);
        firmwareFile.setDescription(this.description);
        firmwareFile.setPushToNewDevices(this.pushToNewDevices);
        firmwareFile.setFile(this.file);
        firmwareFile.setHash(this.hash);
        firmwareFile.updateFirmwareModuleData(new FirmwareModuleData(this.moduleVersionComm, this.moduleVersionFunc,
                this.moduleVersionMa, this.moduleVersionMbus, this.moduleVersionSec)
                        .getVersionsByModule(firmwareModuleRepository, isForSmartMeters));
        return firmwareFile;
    }

    @Override
    public FirmwareFileBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_FILE_FILENAME)) {
            this.withFilename(inputSettings.get(PlatformKeys.FIRMWARE_FILE_FILENAME));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_DESCRIPTION)) {
            this.withDescription(inputSettings.get(PlatformKeys.FIRMWARE_DESCRIPTION));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES)) {
            this.withPushToNewDevices(
                    Boolean.parseBoolean(inputSettings.get(PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES)));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM)) {
            this.withModuleVersionComm(inputSettings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC)) {
            this.withModuleVersionFunc(inputSettings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_MA)) {
            this.withModuleVersionMa(inputSettings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS)) {
            this.withModuleVersionMbus(inputSettings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_SEC)) {
            this.withModuleVersionSec(inputSettings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_SEC));
        }

        if (inputSettings.containsKey(PlatformKeys.FIRMWARE_HASH)) {
            this.withHash(inputSettings.get(PlatformKeys.FIRMWARE_HASH));
        }

        return this;
    }

}
