/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.services;

import com.alliander.osgp.domain.core.entities.GasMeterDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.repositories.GasMeterDeviceRepository;

@Service
@Validated
@Transactional(value = "transactionManager")
public class SmartMeteringDeviceDomainService {

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

    @Autowired
    private GasMeterDeviceRepository gasMeterDeviceRepository;

    public SmartMeter searchSmartMeteringDevice(@Identification final String deviceIdentification)
            throws UnknownEntityException {

        final SmartMeter smartMeteringDevice = this.smartMeteringDeviceRepository
                .findByDeviceIdentification(deviceIdentification);

        if (smartMeteringDevice == null) {
            throw new UnknownEntityException(SmartMeter.class, deviceIdentification);
        }

        return smartMeteringDevice;
    }

    public GasMeterDevice searchGasMeteringDevice(@Identification final String deviceIdentification)
            throws UnknownEntityException {

        final GasMeterDevice gASMeteringDevice = this.gasMeterDeviceRepository
                .findByDeviceIdentification(deviceIdentification);

        if (gASMeteringDevice == null) {
            throw new UnknownEntityException(SmartMeter.class, deviceIdentification);
        }

        return gASMeteringDevice;
    }
}
