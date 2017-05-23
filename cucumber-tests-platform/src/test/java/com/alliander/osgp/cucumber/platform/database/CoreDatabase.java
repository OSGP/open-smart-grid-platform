/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EanRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.RelayStatusRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

@Component
public class CoreDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreDatabase.class);

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepository;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EanRepository eanRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FirmwareRepository firmwareRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private RelayStatusRepository relayStatusRepository;

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: The test-org organization which is used to
     * send authorized requests to the platform.
     */
    @Transactional("txMgrCore")
    public void insertDefaultData() {
        if (this.organisationRepository
                .findByOrganisationIdentification(PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION) == null) {
            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation(PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_ORGANIZATION_DESCRIPTION, PlatformDefaults.DEFAULT_PREFIX,
                    PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
            testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
            testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
            testOrg.setIsEnabled(true);

            this.organisationRepository.save(testOrg);
        }

        // Create default test manufacturer
        final Manufacturer manufacturer = new Manufacturer(PlatformDefaults.DEFAULT_MANUFACTURER_ID,
                PlatformDefaults.DEFAULT_MANUFACTURER_NAME, false);
        this.manufacturerRepository.save(manufacturer);

        // Create the default test model
        DeviceModel deviceModel = new DeviceModel(manufacturer, PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION, true);
        deviceModel = this.deviceModelRepository.save(deviceModel);
    }

    @Transactional("txMgrCore")
    public void prepareDatabaseForScenario() {
        this.batchDeleteAll();
    }

    @Transactional("txMgrCore")
    public void removeLeftOvers() {
        this.normalDeleteAll();
    }

    private void batchDeleteAll() {
        LOGGER.info("Starting batchDeleteAll()");
        this.deviceAuthorizationRepository.deleteAllInBatch();
        this.deviceLogItemRepository.deleteAllInBatch();
        this.scheduledTaskRepository.deleteAllInBatch();
        this.eanRepository.deleteAllEans();
        this.deviceRepository.deleteDeviceOutputSettings();
        this.deviceFirmwareRepository.deleteAllInBatch();
        this.eventRepository.deleteAllInBatch();
        this.smartMeterRepository.deleteAllInBatch();
        this.relayStatusRepository.deleteAllInBatch();
        this.ssldRepository.deleteAllInBatch();
        this.deviceRepository.deleteAllInBatch();
        this.firmwareRepository.deleteAllInBatch();
        this.deviceModelRepository.deleteAllInBatch();
        this.manufacturerRepository.deleteAllInBatch();
        this.organisationRepository.deleteAllInBatch();
    }

    private void normalDeleteAll() {
        LOGGER.info("Starting normalDeleteAll()");
        this.deviceAuthorizationRepository.deleteAll();
        this.deviceLogItemRepository.deleteAll();
        this.scheduledTaskRepository.deleteAll();
        this.eanRepository.deleteAllEans();
        this.deviceRepository.deleteDeviceOutputSettings();
        this.deviceFirmwareRepository.deleteAll();
        this.eventRepository.deleteAll();
        this.smartMeterRepository.deleteAll();
        this.relayStatusRepository.deleteAll();
        this.ssldRepository.deleteAll();
        this.deviceRepository.deleteAll();
        this.firmwareRepository.deleteAll();
        this.deviceModelRepository.deleteAll();
        this.manufacturerRepository.deleteAll();
        this.organisationRepository.deleteAll();
    }
}
