package com.alliander.osgp.webdevicesimulator.application.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.repositories.DeviceRepository;

@Service
public class DeviceManagementService {

    @Resource
    @Autowired
    private DeviceRepository deviceRepository;

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
