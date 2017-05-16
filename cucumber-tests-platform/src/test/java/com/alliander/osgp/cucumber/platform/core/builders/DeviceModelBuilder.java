/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core.builders;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;

public class DeviceModelBuilder implements CucumberBuilder<DeviceModel> {

    private Manufacturer manufacturer;
    private String modelCode;
    private String description;
    private boolean fileStorage;
    private boolean metered;

    public DeviceModelBuilder withManufacturer(final Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public DeviceModelBuilder withModelCode(final String modelCode) {
        this.modelCode = modelCode;
        return this;
    }

    public DeviceModelBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public DeviceModelBuilder withFileStorage(final boolean fileStorage) {
        this.fileStorage = fileStorage;
        return this;
    }

    public DeviceModelBuilder withMetered(final boolean metered) {
        this.metered = metered;
        return this;
    }

    @Override
    public DeviceModel build() {
        final DeviceModel deviceModel = new DeviceModel(this.manufacturer, this.modelCode, this.description,
                this.fileStorage, this.metered);
        return deviceModel;
    }

    @Override
    public DeviceModelBuilder withSettings(final Map<String, String> inputSettings) {

        if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_MODELCODE)) {
            this.withModelCode(inputSettings.get(PlatformKeys.DEVICEMODEL_MODELCODE));
        }

        if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_DESCRIPTION)) {
            this.withDescription(inputSettings.get(PlatformKeys.DEVICEMODEL_DESCRIPTION));
        }

        if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_FILESTORAGE)) {
            this.withFileStorage(Boolean.parseBoolean(inputSettings.get(PlatformKeys.DEVICEMODEL_FILESTORAGE)));
        }

        if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_METERED)) {
            this.withMetered(Boolean.parseBoolean(inputSettings.get(PlatformKeys.DEVICEMODEL_METERED)));
        }

        return this;
    }
}
