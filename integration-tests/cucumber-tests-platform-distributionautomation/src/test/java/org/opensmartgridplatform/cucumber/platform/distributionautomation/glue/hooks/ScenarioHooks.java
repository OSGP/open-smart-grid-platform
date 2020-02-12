/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.hooks;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.database.Database;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.distributionautomation.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

    @Autowired
    private Database databaseSteps;

    @Autowired
    private NotificationService mockNotificationService;

    /**
     * Executed after each scenario.
     *
     * Order 1000 ensures this will be run as one of the first hooks after the
     * scenario.
     */
    @After(order = 1000)
    public void afterScenario() {
        // Destroy scenario context as the scenario is finished.
        ScenarioContext.context = null;
    }

    /**
     * Executed before each scenario.
     *
     * Remove all test related data from the database before each test. Each
     * test should stand on its own. Therefore you should guarantee that the
     * scenario is independent from others.
     *
     * Order 1000 ensures this will be run as one of the first hooks before the
     * scenario.
     */
    @Before(order = 1000)
    public void beforeScenario() {
        this.databaseSteps.prepareDatabaseForScenario();

        this.mockNotificationService.clearAllNotifications();
        // Make sure the scenario context is clean before each test.
        ScenarioContext.context = null;
    }
}
