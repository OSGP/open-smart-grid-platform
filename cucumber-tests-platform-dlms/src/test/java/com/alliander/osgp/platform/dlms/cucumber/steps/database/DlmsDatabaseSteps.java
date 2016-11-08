/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.repositories.DeleteAllDevicesService;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

/**
 *
 */
@Component
public class DlmsDatabaseSteps {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepo;

    @Autowired
    private SmartMeterRepository smartMeterRepo;

    @Autowired
    private DlmsSecurityKeyRepository dlmsDSecurityKeyRepo;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorization;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepo;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DeleteAllDevicesService deleteAllDevicesService;

    /**
     * Before each scenario dlms related stuff needs to be removed.
     */
    @Transactional(transactionManager = "txMgrCore")
    public void prepareDatabaseForScenario() {
        // Remove all data from previous scenario.
        this.deviceAuthorization.deleteAllInBatch();
        this.deleteAllDevicesService.deleteAllDevices();
        this.dlmsDSecurityKeyRepo.deleteAllInBatch();
        this.dlmsDeviceRepo.deleteAllInBatch();
        this.smartMeterRepo.deleteAllInBatch();
        this.meterResponseDataRepo.deleteAllInBatch();
        this.deviceLogItemRepository.deleteAllInBatch();
        this.scheduledTaskRepository.deleteAllInBatch();

        this.insertDefaultData();
    }

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: A default dlms gateway device.
     */
    private void insertDefaultData() {
        // TODO insert here default devices.
    }

}
