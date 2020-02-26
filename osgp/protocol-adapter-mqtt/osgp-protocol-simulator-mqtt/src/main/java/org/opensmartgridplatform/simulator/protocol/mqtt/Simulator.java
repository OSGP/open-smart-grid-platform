/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.moquette.broker.config.MemoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Simulator {

    private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);
    private static final String DEFAULT_BROKER_HOST = "127.0.0.1";
    private static final int DEFAULT_BROKER_PORT = 8883;

    public static void main(final String[] args) throws Exception {
        final Simulator app = new Simulator();
        app.run(args);
    }

    private void run(final String[] args) throws Exception {
        final SimulatorSpec simulatorSpec = this.getSimulatorSpec(args);
        final Broker broker = new Broker(this.getConfig(simulatorSpec));
        broker.start();
        Thread.sleep(simulatorSpec.getStartupPauseMillis());
        final LnaClient lnaClient = new LnaClient(simulatorSpec);
        lnaClient.start();
    }

    private MemoryConfig getConfig(final SimulatorSpec simulatorSpec) {
        final MemoryConfig memoryConfig = new MemoryConfig(new Properties());
        memoryConfig.setProperty("host", simulatorSpec.getBrokerHost());
        memoryConfig.setProperty("port", String.valueOf(simulatorSpec.getBrokerPort()));
        return memoryConfig;
    }

    private SimulatorSpec getSimulatorSpec(final String[] args) throws IOException {
        final SimulatorSpec simulatorSpec;
        if (args.length > 0) {
            final String jsonPath = args[0];
            File jsonFile = new File(jsonPath);
            if (!jsonFile.exists()) {
                final ClassPathResource jsonResource = new ClassPathResource(jsonPath);
                if (jsonResource.exists()) {
                    jsonFile = jsonResource.getFile();
                } else {
                    throw new IllegalArgumentException(
                            String.format("Could not find file or class path resource %s", jsonPath));
                }
            }
            simulatorSpec = new ObjectMapper().readValue(jsonFile, SimulatorSpec.class);
        } else {
            simulatorSpec = new SimulatorSpec(DEFAULT_BROKER_HOST, DEFAULT_BROKER_PORT);
        }
        LOG.info("Simulator spec: {}", simulatorSpec);
        return simulatorSpec;
    }

}
