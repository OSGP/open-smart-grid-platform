/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.builders.entities;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.core.builders.CucumberBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.domain.core.entities.SmartMeter;

public class SmartMeterBuilder extends BaseDeviceBuilder<SmartMeterBuilder> implements CucumberBuilder<SmartMeter> {

    private String supplier;
    private Short channel;
    private String mbusIdentificationNumber;
    private String mbusManufacturerIdentification;
    private String mbusVersion;
    private String mbusDeviceTypeIdentification;

    public SmartMeterBuilder setSupplier(final String supplier) {
        this.supplier = supplier;
        return this;
    }

    public SmartMeterBuilder setChannel(final Short channel) {
        this.channel = channel;
        return this;
    }

    public SmartMeterBuilder setMbusIdentificationNumber(final String value) {
        this.mbusIdentificationNumber = value;
        return this;
    }

    public SmartMeterBuilder setMbusManufacturerIdentification(final String value) {
        this.mbusManufacturerIdentification = value;
        return this;
    }

    public SmartMeterBuilder setMbusVersion(final String value) {
        this.mbusVersion = value;
        return this;
    }

    public SmartMeterBuilder setMbusDeviceTypeIdentification(final String value) {
        this.mbusDeviceTypeIdentification = value;
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
        device.setVersion(this.version);
        device.setDeviceModel(this.deviceModel);
        device.setTechnicalInstallationDate(this.technicalInstallationDate);

        device.setSupplier(this.supplier);
        device.setChannel(this.channel);

        device.setMbusIdentificationNumber(this.mbusIdentificationNumber);
        device.setMbusManufacturerIdentification(this.mbusManufacturerIdentification);
        device.setMbusVersion(this.mbusVersion);
        device.setMbusDeviceTypeIdentification(this.mbusDeviceTypeIdentification);

        return device;
    }

    @Override
    public SmartMeterBuilder withSettings(final Map<String, String> inputSettings) {
        super.withSettings(inputSettings);

        if (inputSettings.containsKey(PlatformSmartmeteringKeys.CHANNEL)) {
            this.setChannel(Short.parseShort(inputSettings.get(PlatformSmartmeteringKeys.CHANNEL)));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.SUPPLIER)) {
            this.setSupplier(inputSettings.get(PlatformSmartmeteringKeys.SUPPLIER));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER)) {
            this.setMbusIdentificationNumber(inputSettings.get(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION)) {
            this.setMbusManufacturerIdentification(
                    inputSettings.get(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_VERSION)) {
            this.setMbusVersion(inputSettings.get(PlatformSmartmeteringKeys.MBUS_VERSION));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION)) {
            this.setMbusDeviceTypeIdentification(
                    inputSettings.get(PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION));
        }

        return this;
    }
}
