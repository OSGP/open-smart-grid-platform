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

import com.alliander.osgp.domain.core.entities.GasMeterDevice;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.repositories.GasMeterDeviceRepository;

/**
 *
 * @author osgp
 * @deprecated temporary GAS meter administration, to be refactored
 */
@Service
@Validated
@Transactional(value = "transactionManager")
@Deprecated
public class GasMeterDeviceDomainService {

    @Autowired
    private GasMeterDeviceRepository GASMeterDeviceRepository;

    public GasMeterDevice searchGASMeterDevice(@Identification final String deviceIdentification)
            throws UnknownEntityException {

        final GasMeterDevice GASMeterDevice = this.GASMeterDeviceRepository
                .findByDeviceIdentification(deviceIdentification);

        if (GASMeterDevice == null) {
            throw new UnknownEntityException(GasMeterDevice.class, deviceIdentification);
        }

        return GASMeterDevice;
    }
}
