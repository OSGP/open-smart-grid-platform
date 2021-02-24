/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.database;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Iec60870Database {

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Transactional("txMgrIec60870")
    public void prepareDatabaseForScenario() {
        this.iec60870DeviceRepository.deleteAllInBatch();
    }

    @Transactional(value = "txMgrIec60870", readOnly = true)
    public boolean isIec60870DeviceTableEmpty() {
        return this.iec60870DeviceRepository.findAll().size() == 0;
    }

    @Transactional("txtMgrIec60870")
    public Iec60870Device addIec60870Device(final Iec60870Device device) {
        return this.iec60870DeviceRepository.save(device);
    }
}
