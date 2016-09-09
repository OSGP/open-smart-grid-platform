/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

import cucumber.api.java.Before;

/**
 * Global hooks for the cucumber tests.
 */
public class GlobalHooks {

    private static boolean dunit = false;

    @Autowired
    private OrganisationRepository organizationRepo;

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
            this.organizationRepo.deleteAll();

            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation("test-org", "Test Organization for all tests", "MAA",
                    PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
            testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
            testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);

            this.organizationRepo.save(testOrg);

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
