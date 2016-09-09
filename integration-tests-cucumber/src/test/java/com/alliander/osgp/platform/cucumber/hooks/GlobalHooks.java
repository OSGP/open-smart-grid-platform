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

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.steps.database.Defaults;

import cucumber.api.java.Before;

/**
 * Global hooks for the cucumber tests.
 */
public class GlobalHooks {

    private static boolean dunit = false;

    @Autowired
    private OrganisationRepository organizationRepo;
    
    @Autowired
    private ManufacturerRepository manufacturerRepo;
    
    @Autowired
    private DeviceModelRepository deviceModelRepo;

    /**
     * Executed once before all scenarios.
     */
    @Before
    public void beforeAll() {
        if (!dunit) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    GlobalHooks.this.afterAll();
                }
            });

            // Do the before all stuff here ...
            // First delete all organizations as we want our own organization to
            // be added.
            cleanRepoSerializable(this.deviceModelRepo);
            cleanRepoSerializable(this.manufacturerRepo);
            cleanRepoAbstractEntity(this.organizationRepo);

            // TODO: Better would be to have some sort of init method in the 
            // steps.database package which will create the nescessary basic 
            // entities.
            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation(
            		Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION, 
            		Defaults.DEFAULT_ORGANIZATION_DESCRIPTION, 
            		Defaults.DEFAULT_PREFIX,
            		PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
			testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
			testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
			testOrg.setIsEnabled(true);

            this.organizationRepo.save(testOrg);
            
            // Create default test manufacturer
            final Manufacturer manufacturer = new Manufacturer(
            		Defaults.DEFAULT_MANUFACTURER_ID, 
            		Defaults.DEFAULT_MANUFACTURER_NAME, 
            		false);
            this.manufacturerRepo.save(manufacturer);
            
            // Create the default test model
            final DeviceModel deviceModel = new DeviceModel(
            		manufacturer, 
            		Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE, 
            		Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);
            this.deviceModelRepo.save(deviceModel);

            dunit = true;
        }
    }

    /**
     * Executed after all scenarios.
     */
    public void afterAll() {
        // Do here the after all stuff...
    }
}
