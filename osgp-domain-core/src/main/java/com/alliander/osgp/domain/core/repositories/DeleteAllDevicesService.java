/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllDevicesService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepository;

    @Autowired
    private FirmwareRepository firmwareRepository;

    public void deleteAllDevices() {
        this.eventRepository.deleteAllInBatch();
        this.deviceFirmwareRepository.deleteAllInBatch();
        this.deviceRepository.deleteAllEans();
        this.deviceRepository.deleteDeviceOutputSettings();
        this.ssldRepository.deleteAllInBatch();
        this.deviceRepository.deleteAllInBatch();
        this.firmwareRepository.deleteAllInBatch();
        this.deviceModelRepository.deleteAllInBatch();
    }
}
