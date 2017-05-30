/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
public class DlmsSimulatorConfig extends AbstractConfig {

    public DlmsSimulatorConfig() {
    }

    @Value("${dynamic.properties.host}")
    private String dynamicPropertiesHost;

    @Value("${dynamic.properties.port}")
    private int dynamicPropertiesPort;

    public String getDynamicPropertiesHost() {
        return this.dynamicPropertiesHost;
    }

    public int getDynamicPropertiesPort() {
        return this.dynamicPropertiesPort;
    }
}
