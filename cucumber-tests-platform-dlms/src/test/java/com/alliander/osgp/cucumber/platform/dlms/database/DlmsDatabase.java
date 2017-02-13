/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.database;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;

/**
 * DLMS related database steps.
 */
@Component
public class DlmsDatabase {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepo;

    @Autowired
    private DlmsSecurityKeyRepository dlmsDSecurityKeyRepo;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepo;

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: A default dlms gateway device.
     */
    private void insertDefaultData() {
        // TODO insert here default devices.
    }

    /**
     * Before each scenario dlms related stuff needs to be removed.
     */
    @Transactional(transactionManager = "txMgrCore")
    public void prepareDatabaseForScenario() {

        this.dlmsDSecurityKeyRepo.deleteAllInBatch();
        this.dlmsDeviceRepo.deleteAllInBatch();
        this.meterResponseDataRepo.deleteAllInBatch();

        this.insertDefaultData();
    }

}
