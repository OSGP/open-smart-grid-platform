/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.database.DatabaseSteps;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

    @Autowired
    private DatabaseSteps databaseSteps;

    /**
     * Executed before each scenario.
     *
     * Remove all stuff from the database before each test. Each test should
     * stand on its own. Therefore you should guarantee that the scenario is
     * complete.
     *
     * Order 0 ensures this will be run as first hook.
     */
    @Before(order = 0)
    public void beforeScenario() {
        this.databaseSteps.prepareDatabaseForScenario();

        // Make sure that the scenario context is clean before each test.
        ScenarioContext.context = null;
    }

    /**
     * Executed after each scenario.
     *
     * Order 99999 ensures this will be run as one of the first hooks after the
     * scenario.
     */
    @After(order = 99999)
    public void afterScenario() {
        // Destroy scenario context as the scenario is finished.
        ScenarioContext.context = null;
    }
}
