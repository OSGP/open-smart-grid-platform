/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.application.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.repositories.DeviceRepository;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.EventNotificationToBeSent;

@Service
public class DeviceManagementService {

    @Resource
    @Autowired
    private DeviceRepository deviceRepository;

    public List<EventNotificationToBeSent> listeventNotificationToBeSent = new ArrayList<>();

    protected void setDeviceRepository(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> findAllDevices() {
        return this.deviceRepository.findAllOrderById();
    }

    public Device findDevice(final Long id) {
        return this.deviceRepository.findOne(id);
    }

    public Device findDevice(final String deviceUid) {
        return this.deviceRepository.findByDeviceUid(deviceUid);
    }

    public Device addDevice(final Device device) {
        return this.deviceRepository.save(device);
    }

    public Device updateDevice(final Device device) {
        return this.deviceRepository.save(device);
    }
}
