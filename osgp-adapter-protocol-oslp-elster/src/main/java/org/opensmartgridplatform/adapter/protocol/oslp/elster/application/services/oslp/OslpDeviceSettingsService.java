/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;

@Service
@Transactional(value = "transactionManager")
public class OslpDeviceSettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpDeviceSettingsService.class);

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    /**
     * Constructor
     */
    public OslpDeviceSettingsService() {
        // Parameterless constructor required for transactions...
    }

    public OslpDevice addDevice(final OslpDevice device) {
        LOGGER.info("add device: {}", device.getDeviceIdentification());

        return this.oslpDeviceRepository.save(device);
    }

    public void removeDevice(final OslpDevice device) {
        LOGGER.info("remove device: {}", device.getDeviceIdentification());

        this.oslpDeviceRepository.delete(device);
    }

    public OslpDevice updateDevice(final OslpDevice device) {
        LOGGER.info("update device: {}", device.getDeviceIdentification());

        return this.oslpDeviceRepository.save(device);
    }

    public OslpDevice updateDeviceAndForceSave(final OslpDevice device) {
        LOGGER.info("update device and force save: {}", device.getDeviceIdentification());

        return this.oslpDeviceRepository.saveAndFlush(device);
    }

    public OslpDevice getDeviceByUid(final String deviceUid) {
        LOGGER.info("get device by UID: {}", deviceUid);

        return this.oslpDeviceRepository.findByDeviceUid(deviceUid);
    }

    public OslpDevice getDeviceByDeviceIdentification(final String deviceIdentification) {
        LOGGER.info("get device by device identification: {}", deviceIdentification);

        return this.oslpDeviceRepository.findByDeviceIdentification(deviceIdentification);
    }

    public List<OslpDevice> getAllDevices() {
        LOGGER.info("get all devices");

        return this.oslpDeviceRepository.findAll();
    }
}
