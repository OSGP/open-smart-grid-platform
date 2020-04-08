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
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Iec60870SingleCommandAsduHandler extends Iec60870AsduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870SingleCommandAsduHandler.class);

    public Iec60870SingleCommandAsduHandler() {
        super(ASduType.C_SC_NA_1);
    }

    @Override
    public void handleAsdu(final Connection connection, final ASdu asdu) throws IOException {
        LOGGER.info("Received single command. Not implemented yet.");
        throw new UnsupportedOperationException();
    }

}
