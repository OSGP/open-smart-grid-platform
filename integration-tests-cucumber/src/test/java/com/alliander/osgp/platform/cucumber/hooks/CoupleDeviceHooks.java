/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;

/**
 * helper class for couple devices to provide database access. It is used to
 * prepare the database beforehand, and test for the existence of specific
 * records afterwards.
 *
 */
@Component
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class CoupleDeviceHooks {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    /**
     * check that the given mbus device is coupled with a gateway device
     *
     * @param deviceId
     *            the gateway device
     * @param mbusDeviceId
     *            the mbus device
     * @return <code>true</code> when the devices are coupled, else
     *         <code>false</code>
     */
    public boolean areDevicesCoupled(final String deviceId, final String mbusDeviceId) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);

        if ((mbusDevice != null) && (mbusDevice.getGatewayDevice() != null)
                && (mbusDevice.getGatewayDevice().getId() != null)) {
            return mbusDevice.getGatewayDevice().getId().equals(device.getId());
        }
        return false;
    }

    public void deCoupleDevices(final String deviceId, final String mbusDeviceId) {
        if (this.areDevicesCoupled(deviceId, mbusDeviceId)) {
            final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
            mbusDevice.updateGatewayDevice(null);
            this.deviceRepository.save(mbusDevice);
        }
    }

    public void coupleDevices(String deviceId, String mbusDeviceId, int channel) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        final SmartMeter mbusSmartMeter = this.smartMeterRepository.findByDeviceIdentification(mbusDeviceId);
        mbusSmartMeter.updateGatewayDevice(device);
        mbusSmartMeter.setChannel((short) channel);
        this.smartMeterRepository.save(mbusSmartMeter);
    }

    public void inactivateDevice(String mbusDeviceId) {
        final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
        mbusDevice.setActivated(false);
        this.deviceRepository.save(mbusDevice);
    }

    public void activateDevice(String mbusDeviceId) {
        final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
        mbusDevice.setActivated(true);
        this.deviceRepository.save(mbusDevice);
    }
}
