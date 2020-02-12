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

    public static void main(final String[] args) throws Exception {
        final Simulator app = new Simulator();
        app.run(args);
    }

    private void run(final String[] args) throws Exception {
        final File json = this.getFile(args);
        final SimulatorSpec simulatorSpec = new ObjectMapper().readValue(json, SimulatorSpec.class);
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

    private File getFile(final String[] args) throws IOException {
        final File spec;
        if (args.length > 0) {
            spec = new File(args[0]);
        } else {
            spec = new ClassPathResource("simulator_spec.json").getFile();
        }
        LOG.info("Simulator spec: {}", spec);
        return spec;
    }

}
