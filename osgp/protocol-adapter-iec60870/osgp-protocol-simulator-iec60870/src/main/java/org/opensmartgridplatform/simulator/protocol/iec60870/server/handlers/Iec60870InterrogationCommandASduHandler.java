/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870ASduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.Iec60870ASduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870InterrogationCommandASduHandler extends Iec60870ASduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870InterrogationCommandASduHandler.class);
    private static final TypeId TYPE_ID = TypeId.C_IC_NA_1;

    @Autowired
    private Iec60870ASduFactory iec60870aSduFactory;

    public Iec60870InterrogationCommandASduHandler() {
        super(TYPE_ID);
    }

    @Override
    public void handleASdu(final Connection connection, final ASdu aSdu) throws IOException {
        LOGGER.info("Received interrogation command. Sending confirmation for ASdu: {}", aSdu);
        connection.sendConfirmation(aSdu);

        final ASdu responseASdu = this.iec60870aSduFactory.createInterrogationCommandResponseASdu();
        LOGGER.info("Processing interrogation command. Sending response ASdu: {}.", responseASdu);
        connection.send(responseASdu);
    }
}
