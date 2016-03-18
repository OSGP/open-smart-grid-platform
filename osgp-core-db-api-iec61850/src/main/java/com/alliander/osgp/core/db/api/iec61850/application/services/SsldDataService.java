/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.iec61850.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;

@Service
@Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
public class SsldDataService {

    @Autowired
    private SsldDataRepository ssldDataRepository;

    public Ssld findDevice(final String deviceIdentification) {
        return this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
    }
}
