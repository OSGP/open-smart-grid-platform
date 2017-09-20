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
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

public class DeviceBuilder extends BaseDeviceBuilder<DeviceBuilder> implements CucumberBuilder<Device> {

    private final DeviceRepository deviceRepository;

    public DeviceBuilder(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public DeviceBuilder withSettings(final Map<String, String> inputSettings) {
        super.withSettings(inputSettings);
        return this;
    }

    @Override
    public Device build() {
        final Device device = new Device(this.deviceIdentification, this.alias, this.containerCity,
                this.containerPostalCode, this.containerStreet, this.containerNumber, this.containerMunicipality,
                this.gpsLatitude, this.gpsLongitude);

        device.updateProtocol(this.protocolInfo);
        device.updateInMaintenance(this.inMaintenance);
        if (this.gatewayDeviceIdentification != null) {
            device.updateGatewayDevice(
                    this.deviceRepository.findByDeviceIdentification(this.gatewayDeviceIdentification));
        }
        device.setVersion(this.version);
        device.setDeviceModel(this.deviceModel);
        device.setTechnicalInstallationDate(this.technicalInstallationDate);
        // updateRegistrationData sets the status to IN_USE, so setting of any
        // other status has to be done after that.
        device.updateRegistrationData(this.networkAddress, this.deviceType);
        device.setDeviceLifecycleStatus(this.deviceLifeCycleStatus);

        return device;
    }
}
