/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.steps.database.DatabaseSteps;

import cucumber.api.java.Before;

/**
 * Global hooks for the cucumber tests.
 */
public class GlobalHooks {

    private static boolean executedOnce = false;
    
    @Autowired
    DatabaseSteps databaseSteps;

    /**
     * Executed once before all scenarios.
     */
    @Before
    public void beforeAll() {
        if (!executedOnce) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    GlobalHooks.this.afterAll();
                }
            });

            // TODO: Do the global stuff which has to be executed only once here.
        	databaseSteps.prepareDatabaseForTestRun();

            executedOnce = true;
        }
    }

    /**
     * Executed after all scenarios.
     */
    public void afterAll() {
        // Do here the after all stuff...
    }
}
