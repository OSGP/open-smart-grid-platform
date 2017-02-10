/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.database.DlmsDatabase;
import com.alliander.osgp.cucumber.platform.dlms.support.ServiceEndpoint;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks extends GlueBase {

    @Value("${alarm.notifications.host}")
    private String alarmNotificationsHost;

    @Value("${alarm.notifications.port}")
    private int alarmNotificationsPort;

    @Autowired
    private DlmsDatabase dlmsDatabaseSteps;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Value("${service.endpoint.host}")
    private String serviceEndpointHost;

    /**
     * Executed after each scenario.
     */
    @After
    public void afterScenario() {
        // Destroy scenario context as the scenario is finished.
        ScenarioContext.context = null;
    }

    /**
     * Executed before each scenario.
     *
     * Remove all stuff from the database before each test. Each test should
     * stand on its own. Therefore you should guarantee that the scenario is
     * complete.
     */
    @Before
    public void beforeScenario() {
        this.dlmsDatabaseSteps.prepareDatabaseForScenario();
        this.prepareServiceEndpoint();
    }

    private void prepareServiceEndpoint() {
        this.serviceEndpoint.setServiceEndpoint(this.serviceEndpointHost);
        this.serviceEndpoint.setAlarmNotificationsHost(this.alarmNotificationsHost);
        this.serviceEndpoint.setAlarmNotificationsPort(this.alarmNotificationsPort);
    }
}