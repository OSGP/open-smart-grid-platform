/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.openiec61850.SclParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RtuSimulatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtuSimulatorConfig.class);

    @Bean
    public RtuSimulator rtuSimulator(@Value("${rtu.icd:Pampus_v0.4.5.icd}") final String icdFilename,
            @Value("${rtu.port:60102}") final Integer port,
            @Value("${rtu.serverName:WAGO61850Server}") final String serverName) throws IOException {
        final InputStream icdFile = ClassLoader.getSystemResourceAsStream(icdFilename);

        try {
            final RtuSimulator rtuSimulator = new RtuSimulator(port, icdFile, serverName);
            rtuSimulator.start();
            return rtuSimulator;
        } catch (final SclParseException e) {
            LOGGER.warn("Error parsing SCL/ICD file {}", e);
        } finally {
            icdFile.close();
        }

        return null;
    }
}
