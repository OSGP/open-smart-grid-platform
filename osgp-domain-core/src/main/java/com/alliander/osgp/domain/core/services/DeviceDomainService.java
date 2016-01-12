/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.validation.Identification;

@Service
@Validated
@Transactional(value = "transactionManager")
public class DeviceDomainService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SsldRepository ssldRepository;

    public Device searchDevice(@Identification final String deviceIdentification) throws UnknownEntityException {

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device == null) {
            throw new UnknownEntityException(Device.class, deviceIdentification);
        }

        return device;
    }

    public Device searchActiveDevice(@Identification final String deviceIdentification)
            throws UnregisteredDeviceException, UnknownEntityException {

        final Device device = this.searchDevice(deviceIdentification);
        final Ssld ssld = this.ssldRepository.findOne(device.getId());

        // Note: since this code is still specific for SSLD / PSLD, this null
        // check is needed.
        if (ssld != null) {
            if (!device.isActivated() || !ssld.isPublicKeyPresent()) {
                throw new UnregisteredDeviceException(deviceIdentification);
            }
        }

        if (!device.isActivated()) {
            throw new UnregisteredDeviceException(deviceIdentification);
        }
        return device;
    }
}
