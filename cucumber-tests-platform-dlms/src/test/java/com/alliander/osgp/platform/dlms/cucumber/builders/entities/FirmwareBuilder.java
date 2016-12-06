/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class FirmwareBuilder implements CucumberBuilder<Firmware> {

    private String supplier;

    private Short channel;

    public FirmwareBuilder setSupplier(final String supplier) {
        this.supplier = supplier;
        return this;
    }

    public FirmwareBuilder setChannel(final Short channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public Firmware build() {
        final Firmware firmware = new Firmware();

        return firmware;
    }

    @Override
    public FirmwareBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.CHANNEL)) {
            this.setChannel(Short.parseShort(inputSettings.get(Keys.CHANNEL)));
        }
        if (inputSettings.containsKey(Keys.SUPPLIER)) {
            this.setSupplier(inputSettings.get(Keys.SUPPLIER));
        }

        return this;
    }
}
