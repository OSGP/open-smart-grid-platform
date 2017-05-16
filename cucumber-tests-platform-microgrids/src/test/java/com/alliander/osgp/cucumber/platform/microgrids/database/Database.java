/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.domain.microgrids.repositories.TaskRepository;

@Component
public class Database {

    @Autowired
    private Iec61850DeviceRepository iec61850DeviceRepository;

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    private void insertDefaultData() {
    }

    @Transactional("txMgrCoreMicrogrids")
    public void prepareDatabaseForScenario() {
        // Then remove stuff from osgp_adapter_protocol_iec61850
        this.iec61850DeviceRepository.deleteAll();

        // Then remove stuff from the osgp_adapter_ws_microgrids
        this.rtuResponseDataRepository.deleteAll();

        // Now remove all from the core.
        this.taskRepository.deleteAll();
        this.rtuDeviceRepository.deleteAll();

        this.insertDefaultData();
    }
}
