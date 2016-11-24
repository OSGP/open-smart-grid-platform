/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class SmartMeterBuilder extends BaseDeviceBuilder<SmartMeterBuilder> implements CucumberBuilder<SmartMeter> {

    private String supplier;

    private Short channel;

    public SmartMeterBuilder setSupplier(final String supplier) {
        this.supplier = supplier;
        return this;
    }

    public SmartMeterBuilder setChannel(final Short channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public SmartMeter build() {
        final SmartMeter device = new SmartMeter(this.deviceIdentification, this.alias, this.containerCity,
                this.containerPostalCode, this.containerStreet, this.containerNumber, this.containerMunicipality,
                this.gpsLatitude, this.gpsLongitude);

        device.setActivated(this.isActivated);
        device.updateRegistrationData(this.networkAddress, this.deviceType);

        // After updateRegistrationData because that sets active to true again.
        device.setActive(this.isActive);
        device.updateProtocol(this.protocolInfo);
        device.updateInMaintenance(this.inMaintenance);
        device.updateGatewayDevice(this.gatewayDevice);
        device.setVersion(this.version);
        device.setDeviceModel(this.deviceModel);
        device.setTechnicalInstallationDate(this.technicalInstallationDate);

        device.setSupplier(this.supplier);
        device.setChannel(this.channel);

        return device;
    }

    @Override
    public SmartMeterBuilder withSettings(final Map<String, String> inputSettings) {
        super.withSettings(inputSettings);

        if (inputSettings.containsKey(Keys.CHANNEL)) {
            this.setChannel(Short.parseShort(inputSettings.get(Keys.CHANNEL)));
        }
        if (inputSettings.containsKey(Keys.SUPPLIER)) {
            this.setSupplier(inputSettings.get(Keys.SUPPLIER));
        }

        return this;
    }
}
