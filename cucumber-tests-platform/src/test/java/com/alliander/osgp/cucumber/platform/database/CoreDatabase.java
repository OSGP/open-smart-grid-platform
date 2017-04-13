/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.core.wait.Wait;
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
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.domain.microgrids.repositories.TaskRepository;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

@Component
public class CoreDatabase {

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
    private Iec61850DeviceRepository iec61850DeviceRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: The test-org organization which is used to
     * send authorized requests to the platform.
     */
    @Transactional
    private void insertDefaultData() {
        if (this.organisationRepository
                .findByOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION) == null) {
            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
                    Defaults.DEFAULT_ORGANIZATION_DESCRIPTION, Defaults.DEFAULT_PREFIX, PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
            testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
            testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
            testOrg.setIsEnabled(true);

            this.organisationRepository.save(testOrg);
        }

        // Create default test manufacturer
        final Manufacturer manufacturer = new Manufacturer(Defaults.DEFAULT_MANUFACTURER_ID,
                Defaults.DEFAULT_MANUFACTURER_NAME, false);
        this.manufacturerRepository.save(manufacturer);

        // Create the default test model
        DeviceModel deviceModel = new DeviceModel(manufacturer, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
                Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION, true);
        deviceModel = this.deviceModelRepository.save(deviceModel);
    }

    @Transactional("txMgrCore")
    public void prepareDatabaseForScenario() {
        Wait.until(() -> {
            // First remove stuff from osgp_adapter_protocol_oslp
            this.oslpDeviceRepository.deleteAllInBatch();

            // Then remove stuff from osgp_adapter_protocol_iec61850
            this.iec61850DeviceRepository.deleteAllInBatch();

            // Then remove stuff from the osgp_adapter_ws_microgrids
            this.rtuResponseDataRepository.deleteAllInBatch();

            // Then remove stuff from osgp_core
            this.taskRepository.deleteAll();
            this.deviceAuthorizationRepository.deleteAllInBatch();
            this.deviceLogItemRepository.deleteAllInBatch();
            this.scheduledTaskRepository.deleteAllInBatch();
            this.eanRepository.deleteAllEans();
            this.deviceRepository.deleteDeviceOutputSettings();
            this.deviceFirmwareRepository.deleteAllInBatch();
            this.eventRepository.deleteAllInBatch();
            this.smartMeterRepository.deleteAllInBatch();
            this.ssldRepository.deleteAllInBatch();
            this.rtuDeviceRepository.deleteAllInBatch();
            this.deviceRepository.deleteAllInBatch();
            this.firmwareRepository.deleteAllInBatch();
            this.deviceModelRepository.deleteAllInBatch();
            this.manufacturerRepository.deleteAllInBatch();
            this.organisationRepository.deleteAllInBatch();
        });

        this.insertDefaultData();
    }
}
