/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.database.DlmsDatabase;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation.DeviceSimulatorSteps;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ServiceEndpoint;

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
    private DeviceSimulatorSteps deviceSimulatorSteps;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Value("${service.endpoint.host}")
    private String serviceEndpointHost;

    @Value("${dynamic.properties.host}")
    private String dynamicPropertiesHost;

    @Value("${dynamic.properties.port}")
    private int dynamicPropertiesPort;

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
     * Remove all stuff from the database before each test. Each test should
     * stand on its own. Therefore you should guarantee that the scenario is
     * complete.
     *
     * Order 1000 ensures this will be run as one of the first hooks before the
     * scenario.
     */
    @Before(order = 1000)
    public void beforeScenario() {
        // TODO uncomment when slim-975 is deployed and port 4567 is opened on
        // the machine where device-simulator is running.
        this.deviceSimulatorSteps.removeAllTemporaryPropertiesFiles(this.dynamicPropertiesHost,
                this.dynamicPropertiesPort);

        this.dlmsDatabaseSteps.prepareDatabaseForScenario();
        this.prepareServiceEndpoint();
    }

    private void prepareServiceEndpoint() {
        this.serviceEndpoint.setServiceEndpoint(this.serviceEndpointHost);
        this.serviceEndpoint.setAlarmNotificationsHost(this.alarmNotificationsHost);
        this.serviceEndpoint.setAlarmNotificationsPort(this.alarmNotificationsPort);
    }
}