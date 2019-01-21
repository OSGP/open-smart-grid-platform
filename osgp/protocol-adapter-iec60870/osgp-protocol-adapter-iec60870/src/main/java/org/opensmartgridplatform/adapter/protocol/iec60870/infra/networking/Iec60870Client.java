/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Client.class);
    // private static final String COULD_NOT_EXECUTE_COMMAND = "Could not
    // execute command";

    @Autowired
    private int iec60870PortClient;

    @Autowired
    private int iec60870PortClientLocal;

    @Autowired
    private int maxRedeliveriesForIec60870Requests;

    @Autowired
    private int maxRetryCount;

    @PostConstruct
    private void init() {
        LOGGER.info(
                "portClient: {}, portClientLocal: {}, iec60870SsldPortServer: {}, maxRetryCount: {}, maxRedeliveriesForIec60870Requests: {}",
                this.iec60870PortClient, this.iec60870PortClientLocal, this.maxRetryCount,
                this.maxRedeliveriesForIec60870Requests);
    }

    // TODO: implement connect
    // TODO: implement disconnect
    // TODO: send command with retry
}
