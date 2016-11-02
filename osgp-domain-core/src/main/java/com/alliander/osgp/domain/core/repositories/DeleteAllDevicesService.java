package com.alliander.osgp.domain.core.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllDevicesService {

    @Autowired
    private DeviceRepository deviceRepositry;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private EventRepository eventRepository;

    public void deleteAllDevices() {
        this.ssldRepository.deleteAllInBatch();
        this.eventRepository.deleteAllInBatch();
        this.deviceRepositry.deleteAllEans();
        this.deviceRepositry.deleteDeviceOutputSettings();
        this.deviceRepositry.deleteAllInBatch();
    }
}
