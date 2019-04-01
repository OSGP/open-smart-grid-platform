/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870;

import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackageClasses = (org.opensmartgridplatform.iec60870.Iec60870Server.class))
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = false)
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SimulatorProtocolIec60870/config}", ignoreResourceNotFound = true)
public class Iec60870SimulatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870SimulatorConfig.class);

    @Autowired
    private Iec60870ConnectionRegistry iec60870ConnectionRegistry;

    @Autowired
    private Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;

    @Value("${iec60870.simulator.connection.timeout}")
    private int connectionTimeout;

    @Bean(destroyMethod = "stop")
    public Iec60870Server iec60870Server() {
        LOGGER.debug("Creating IEC60870 Simulator Bean.");
        final Iec60870Server server = new Iec60870Server(this.iec60870ServerEventListener());

        LOGGER.debug("Starting IEC60870 Simulator.");
        server.start();

        return server;
    }

    @Bean
    public Iec60870ServerEventListener iec60870ServerEventListener() {
        return new Iec60870ServerEventListener(this.iec60870ConnectionRegistry, this.iec60870ASduHandlerRegistry,
                this.connectionTimeout);
    }
}
