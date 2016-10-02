/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.dlms.cucumber.steps.database.DlmsDatabaseSteps;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

	@Autowired
    private DlmsDatabaseSteps databaseSteps;

    /**
     * Executed before each scenario.
     *
     * Remove all stuff from the database before each test. Each test should
     * stand on its own. Therefore you should guarantee that the scenario is
     * complete.
     */
    @Before
    public void beforeScenario() {
    	databaseSteps.prepareDatabaseForScenario();
    }

    /**
     * Executed after each scenario.
     */
    @After
    public void afterScenario() {
        // Destroy scenario context as the scenario is finished.
        ScenarioContext.context = null;
    }
}
