/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class Iec60870AsduGeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870AsduGeneratorService.class);

    private Iec60870ASduFactory asduFactory = new Iec60870ASduFactory();
    private Iec60870ConnectionRegistry connectionRegistry;

    public Iec60870AsduGeneratorService(final Iec60870ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    @Scheduled(cron = "${job.asdu.generator.cron:0 0/1 * * * ?}", zone = "UTC")
    public void generate() {
        LOGGER.info("Generating new ASDU.");

        try {
            final ASdu asdu = this.asduFactory.createShortFloatingPointMeasurementAsdu();
            for (final Connection connection : this.connectionRegistry.getAllConnections()) {
                connection.send(asdu);
            }
        } catch (final IOException e) {
            LOGGER.error("Exception occurred: ", e);
        }
    }

}
