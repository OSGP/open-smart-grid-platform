/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ConnectionEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHealthStatusListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusListener.class);

    @Override
    public void connectionClosed(final IOException e) {
        LOGGER.info("Connection to the device was closed", e);
    }

    @Override
    public void newASdu(final ASdu incomingAsdu) {
        LOGGER.info("Received the following ASDU for GetHealthStatus: {}", incomingAsdu);
    }

}
