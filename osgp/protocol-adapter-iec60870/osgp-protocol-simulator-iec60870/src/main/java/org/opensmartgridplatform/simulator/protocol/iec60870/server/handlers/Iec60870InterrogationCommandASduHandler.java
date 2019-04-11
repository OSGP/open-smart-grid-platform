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
import org.opensmartgridplatform.iec60870.Iec60870ASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870ASduFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870InterrogationCommandASduHandler extends Iec60870ASduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870InterrogationCommandASduHandler.class);
    private static final TypeId TYPE_ID = TypeId.C_IC_NA_1;

    @Autowired
    private Iec60870ASduFactory iec60870AsduFactory;

    public Iec60870InterrogationCommandASduHandler() {
        super(TYPE_ID);
    }

    @Override
    public void handleASdu(final Connection connection, final ASdu asdu) throws IOException {
        LOGGER.info("Received interrogation command. Sending confirmation for ASDU: {}", asdu);
        connection.sendConfirmation(asdu);

        final ASdu responseAsdu = this.iec60870AsduFactory.createInterrogationCommandResponseAsdu();
        LOGGER.info("Processing interrogation command. Sending response ASDU: {}.", responseAsdu);
        connection.send(responseAsdu);

        final ASdu terminationAsdu = this.iec60870AsduFactory.createActivationTerminationResponseAsdu();
        LOGGER.info("Finished processing interrogation command. Sending termination ASDU: {}", terminationAsdu);
        connection.send(asdu);

    }
}
