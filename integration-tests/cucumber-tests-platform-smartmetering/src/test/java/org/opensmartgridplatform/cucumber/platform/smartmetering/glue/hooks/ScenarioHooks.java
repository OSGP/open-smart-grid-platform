/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.hooks;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.database.DlmsDatabase;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator.DeviceSimulatorSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.ReplaceKeysSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ServiceEndpoint;

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
    private ReplaceKeysSteps replaceKeysSteps;

    @Autowired
    private DeviceSimulatorSteps deviceSimulatorSteps;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Value("${service.endpoint.host}")
    private String serviceEndpointHost;

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
        this.deviceSimulatorSteps.clearDlmsAttributeValues();
        this.dlmsDatabaseSteps.prepareDatabaseForScenario();
        this.prepareServiceEndpoint();
    }

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

    @After("@ResetKeysOnDevice")
    public void resetKeysScenario() throws Throwable {
        final Map<String, String> settings = this.initSettings();
        final Map<String, String> responseParameters = this.initResponseParameters();

        this.replaceKeysSteps.theReplaceKeysRequestIsReceived(settings);
        this.replaceKeysSteps.theReplaceKeysResponseShouldBeReturned(responseParameters);
    }

    private void prepareServiceEndpoint() {
        this.serviceEndpoint.setServiceEndpoint(this.serviceEndpointHost);
        this.serviceEndpoint.setAlarmNotificationsHost(this.alarmNotificationsHost);
        this.serviceEndpoint.setAlarmNotificationsPort(this.alarmNotificationsPort);
    }

    private Map<String, String> initSettings() {
        return new HashMap<String, String>() {
            {
                this.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
                this.put(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY, PlatformSmartmeteringDefaults.SECURITY_KEY_A_XML);
                this.put(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY, PlatformSmartmeteringDefaults.SECURITY_KEY_E_XML);
            }
        };
    }

    private Map<String, String> initResponseParameters() {
        return new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                this.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
                this.put(PlatformKeys.KEY_RESULT, PlatformDefaults.EXPECTED_RESULT_OK);
            }
        };
    }
}