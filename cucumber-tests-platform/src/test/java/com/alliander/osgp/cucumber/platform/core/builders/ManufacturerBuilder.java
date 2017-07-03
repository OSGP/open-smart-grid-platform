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
import com.alliander.osgp.domain.core.entities.Manufacturer;

public class ManufacturerBuilder implements CucumberBuilder<Manufacturer> {

    private String id = PlatformDefaults.DEFAULT_MANUFACTURER_ID;
    private String name = PlatformDefaults.DEFAULT_MANUFACTURER_NAME;
    private boolean usePrefix = PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX;

    public ManufacturerBuilder withId(final String id) {
        this.id = id;
        return this;
    }

    public ManufacturerBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public ManufacturerBuilder withUsePrefix(final boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

    @Override
    public Manufacturer build() {
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerId(this.id);
        manufacturer.setName(this.name);
        manufacturer.setUsePrefix(this.usePrefix);
        return manufacturer;
    }

    @Override
    public ManufacturerBuilder withSettings(final Map<String, String> inputSettings) {

        if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_ID)) {
            this.withId(inputSettings.get(PlatformKeys.MANUFACTURER_ID));
        }

        if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_NAME)) {
            this.withName(inputSettings.get(PlatformKeys.MANUFACTURER_NAME));
        }

        if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_USE_PREFIX)) {
            this.withUsePrefix(Boolean.parseBoolean(inputSettings.get(PlatformKeys.MANUFACTURER_USE_PREFIX)));
        }

        return this;
    }
}
