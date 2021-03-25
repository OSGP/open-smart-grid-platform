/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870;

import org.opensmartgridplatform.iec60870.Iec60870AsduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation.DefaultControlledStationConfig;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.lightmeasurementdevice.LightMeasurementDeviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = { Iec60870Server.class })
@EnableScheduling
@Import({ DefaultControlledStationConfig.class, LightMeasurementDeviceConfig.class })
@PropertySource(value = "classpath:osgp-protocol-simulator-iec60870.properties", ignoreResourceNotFound = false)
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SimulatorProtocolIec60870/config}", ignoreResourceNotFound = true)
public class Iec60870SimulatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870SimulatorConfig.class);

    @Value("${iec60870.simulator.connection.timeout}")
    private int connectionTimeout;

    @Value("${iec60870.simulator.sendEndOfInitialization:false}")
    private boolean sendEndOfInitialization;

    @Value("${iec60870.simulator.port:2404}")
    private int port;

    @Bean(destroyMethod = "stop")
    public Iec60870Server iec60870Server(final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
            final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry) {
        LOGGER.debug("Creating IEC60870 Simulator Bean.");

        final Iec60870Server server = new Iec60870Server(new Iec60870ServerEventListener(iec60870ConnectionRegistry,
                iec60870AsduHandlerRegistry, this.connectionTimeout, this.sendEndOfInitialization), this.port);

        LOGGER.debug("Starting IEC60870 Simulator.");
        server.start();

        return server;
    }
}
