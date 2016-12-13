/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.core.builders;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class ManufacturerBuilder implements CucumberBuilder<Manufacturer> {

    private String name;
    private boolean usePrefix;

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
        manufacturer.setName(this.name);
        manufacturer.setUsePrefix(this.usePrefix);
        return manufacturer;
    }

    @Override
    public ManufacturerBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.MANUFACTURER_NAME)) {
            this.withName(inputSettings.get(Keys.MANUFACTURER_NAME));
        }

        if (inputSettings.containsKey(Keys.MANUFACTURER_USE_PREFIX)) {
            this.withUsePrefix(Boolean.parseBoolean(inputSettings.get(Keys.MANUFACTURER_USE_PREFIX)));
        }

        return this;
    }
}
