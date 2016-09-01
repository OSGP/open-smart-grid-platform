/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;
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
public class CoupleDeviceHooks {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    public boolean areDevicesCoupled(final String deviceId, final String mbusDeviceId) {
        return this.areDevicesCoupled(deviceId, mbusDeviceId, null);
    }

    /**
     * check that the given mbus device is coupled with a gateway device
     *
     * @param deviceId
     *            the gateway device
     * @param mbusDeviceId
     *            the mbus device
     * @param mbusChannel
     *            the mbus Channel
     * @return <code>true</code> when the devices are coupled, else
     *         <code>false</code>
     */
    public boolean areDevicesCoupled(final String deviceId, final String mbusDeviceId, Short mbusChannel) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(mbusDeviceId);

        return this.areDevicesCoupled(mbusDevice, device) && this.isMbusChannelEqual(mbusChannel, mbusDevice);

    }

    private boolean areDevicesCoupled(SmartMeter mbusDevice, Device device) {
        return (mbusDevice != null)
                && (device != null)
                && (mbusDevice.getGatewayDevice() != null)
                && ((mbusDevice.getGatewayDevice().getId() != null) && mbusDevice.getGatewayDevice().getId()
                        .equals(device.getId()));
    }

    private boolean isMbusChannelEqual(Short mbusChannel, final SmartMeter mbusDevice) {
        return (mbusChannel == null)
                || ((mbusDevice.getChannel() != null) && mbusDevice.getChannel().equals(mbusChannel));
    }

    public void decoupleDevices(final String deviceId, final String mbusDeviceId) {
        if (this.areDevicesCoupled(deviceId, mbusDeviceId)) {
            final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
            mbusDevice.updateGatewayDevice(null);
            this.deviceRepository.save(mbusDevice);
        }
    }

    public void coupleDevices(String eDevice, String mbusDevice, Short channel) {
        final Device device = this.deviceRepository.findByDeviceIdentification(eDevice);
        final SmartMeter mbusSmartMeter = this.smartMeterRepository.findByDeviceIdentification(mbusDevice);
        mbusSmartMeter.updateGatewayDevice(device);
        mbusSmartMeter.setChannel(channel);
        this.smartMeterRepository.save(mbusSmartMeter);
    }

    public void clearChannelForSmartMeterDevice(String mbusDeviceIdentification) {
        final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(mbusDeviceIdentification);
        mbusDevice.setChannel(null);
        this.smartMeterRepository.save(mbusDevice);
    }

}
