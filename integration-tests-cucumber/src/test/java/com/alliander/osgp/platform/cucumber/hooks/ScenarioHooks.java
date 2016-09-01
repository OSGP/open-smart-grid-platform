/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

    private DeviceRepository deviceRepository;
    private DlmsDeviceRepository dlmsDeviceRepository;
    private DeviceAuthorizationRepository deviceAuthorizationRepository;
    private OrganisationRepository organizationRepository;

    /**
     * Executed before each scenario.
     * 
     * Remove all stuff from the database before each test. Each test should stand on its own. Therefore
     * you should guarantee that the scenario is complete.
     */
    @Before
    public void beforeScenario() {

        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");

        // Get all the repositories.
        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.dlmsDeviceRepository = context.getBean(DlmsDeviceRepository.class);
        this.deviceAuthorizationRepository = context.getBean(DeviceAuthorizationRepository.class);
        this.organizationRepository = context.getBean(OrganisationRepository.class);

        // Remove all data from previous scenario.
        this.deviceAuthorizationRepository.deleteAll();
        this.deviceRepository.deleteAll();
        this.dlmsDeviceRepository.deleteAll();

        // TODO Remove here all organizations except test-org.
        this.organizationRepository.deleteAll();
        Organisation testOrg = new Organisation("test-org", "Test Organization for all tests", "MAA", PlatformFunctionGroup.ADMIN);
		testOrg.addDomain(PlatformDomain.COMMON);
		organizationRepository.save(testOrg);
		//this.organizationRepository.deleteAllExcept("test-org");
        //for (Organisation organisation : this.organizationRepository.findAll()) {
        //	if (organisation.getOrganisationIdentification() != "test-org") {
        //    	this.organizationRepository.delete(organisation);
        //	}
        //}
        
        // TODO: Clean all other repositories ....

        context.close();
    }
    
    /**
     * Executed after each scenario.
     */
    @After
    public void afterScenario() {
    	// Nothing to do yet.
    }
}
