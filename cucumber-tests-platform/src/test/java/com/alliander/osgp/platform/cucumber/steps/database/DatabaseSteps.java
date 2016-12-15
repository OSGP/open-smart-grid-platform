/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoAbstractEntity;
import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoSerializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeleteAllDevicesService;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

@Component
public class DatabaseSteps {

    @Autowired
    private OrganisationRepository organisationRepo;

    @Autowired
    private ManufacturerRepository manufacturerRepo;

    @Autowired
    private DeviceModelRepository deviceModelRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private SmartMeterRepository smartMeterRepo;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepo;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepo;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private DeleteAllDevicesService deleteAllDevicesService;

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: The test-org organization which is used to
     * send authorized requests to the platform.
     */
    @Transactional
    private void insertDefaultData() {
        if (this.organisationRepo.findByOrganisationIdentification(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION) == null) {
            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION,
                    Defaults.DEFAULT_ORGANISATION_DESCRIPTION, Defaults.DEFAULT_PREFIX, PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
            testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
            testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
            testOrg.setIsEnabled(true);

            this.organisationRepo.save(testOrg);
        }

        // Create default test manufacturer
        final Manufacturer manufacturer = new Manufacturer(Defaults.DEFAULT_MANUFACTURER_ID,
                Defaults.DEFAULT_MANUFACTURER_NAME, false);
        this.manufacturerRepo.save(manufacturer);

        // Create the default test model
        DeviceModel deviceModel = new DeviceModel(manufacturer, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
                Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION, true);
        deviceModel = this.deviceModelRepo.save(deviceModel);
    }

    @Transactional("txMgrCore")
    public void prepareDatabaseForScenario() {
        cleanRepoAbstractEntity(this.oslpDeviceRepo);
        cleanRepoAbstractEntity(this.deviceAuthorizationRepo);
        this.deleteAllDevicesService.deleteAllDevices();
        cleanRepoSerializable(this.smartMeterRepo);
        cleanRepoSerializable(this.manufacturerRepo);
        cleanRepoSerializable(this.organisationRepo);

        this.insertDefaultData();
    }
}
