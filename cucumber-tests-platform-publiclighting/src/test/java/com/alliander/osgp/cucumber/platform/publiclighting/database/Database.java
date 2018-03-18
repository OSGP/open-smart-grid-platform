/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;

@Component
public class Database {

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Transactional
    private void insertDefaultData() {
    }

    @Transactional("txMgrOslp")
    public void prepareDatabaseForScenario() {
        // First remove stuff from osgp_adapter_protocol_oslp
        this.oslpDeviceRepository.deleteAllInBatch();

        this.insertDefaultData();
    }
}
