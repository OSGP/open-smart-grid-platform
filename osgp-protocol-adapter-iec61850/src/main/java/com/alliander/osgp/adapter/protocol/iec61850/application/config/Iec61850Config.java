/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolIec61850/config}")
public class Iec61850Config {

    private static final String PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT = "iec61850.timeout.connect";
    private static final String PROPERTY_NAME_IEC61850_PORT_CLIENT = "iec61850.port.client";
    private static final String PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL = "iec61850.port.clientlocal";
    private static final String PROPERTY_NAME_IEC61850_PORT_SERVER = "iec61850.port.server";

    @Resource
    private Environment environment;

    @Bean
    public int connectionTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT));
    }

    @Bean
    public int iec61850PortClient() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_CLIENT));
    }

    @Bean
    public int iec61850PortClientLocal() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL));
    }

    @Bean
    public int iec61850PortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_SERVER));
    }
}
