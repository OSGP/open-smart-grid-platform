/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoAbstractEntity;
import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoSerializable;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.database.Defaults;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

    private final Logger LOGGER = LoggerFactory.getLogger(ScenarioHooks.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepo;

    @Autowired
    private DeviceModelRepository deviceModelRepo;

    /**
     * Executed before each scenario.
     *
     * Remove all stuff from the database before each test. Each test should
     * stand on its own. Therefore you should guarantee that the scenario is
     * complete.
     */
    @Before
    public void beforeScenario() {
        this.clearDatabase();
    }

    /**
     * Executed after each scenario.
     */
    @After
    public void afterScenario() {
        // Destroy scenario context as the scenario is finished.
        ScenarioContext.context = null;
    }

    /**
     * Clears the database except for the normal data.
     */
    private void clearDatabase() {
        // Remove all data from previous scenario.
        cleanRepoAbstractEntity(this.deviceAuthorizationRepository);
        cleanRepoSerializable(this.deviceRepository);
        cleanRepoSerializable(this.deviceModelRepo);
        cleanRepoSerializable(this.manufacturerRepo);
        cleanRepoAbstractEntity(this.organizationRepository);
        cleanRepoAbstractEntity(this.dlmsDeviceRepository);
        cleanRepoAbstractEntity(this.oslpDeviceRepository);

        // TODO: Better would be to have some sort of init method in the
        // steps.database package which will create the nescessary basic
        // entities.
        // TODO Remove here all organizations except test-org. In GlobalHooks,
        // the default data will be added
        // in the database. The stuff here should not remove that.
        // Create test organization used within the tests.
        final Organisation testOrg = new Organisation(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_DESCRIPTION, Defaults.DEFAULT_PREFIX, PlatformFunctionGroup.ADMIN);
        testOrg.addDomain(PlatformDomain.COMMON);
        testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
        testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
        testOrg.setIsEnabled(true);

        this.organizationRepository.save(testOrg);

        // Create default test manufacturer
        final Manufacturer manufacturer = new Manufacturer(Defaults.DEFAULT_MANUFACTURER_ID,
                Defaults.DEFAULT_MANUFACTURER_NAME, false);
        this.manufacturerRepo.save(manufacturer);

        // Create the default test model
        final DeviceModel deviceModel = new DeviceModel(manufacturer, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
                Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);
        this.deviceModelRepo.save(deviceModel);

        // TODO: Clean all other repositories ....
    }
}
