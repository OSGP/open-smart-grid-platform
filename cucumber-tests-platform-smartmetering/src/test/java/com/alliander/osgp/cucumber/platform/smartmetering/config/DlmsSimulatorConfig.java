/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.config;

import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
public class DlmsSimulatorConfig extends AbstractConfig {

    public DlmsSimulatorConfig() {
    }

    @Value("${dynamic.properties.base.url}")
    private String dynamicPropertiesBaseUrl;

    @Bean
    public SimulatorTriggerClient simulatorTriggerClient() throws SimulatorTriggerClientException {

        return new SimulatorTriggerClient(this.dynamicPropertiesBaseUrl);
    }
}
