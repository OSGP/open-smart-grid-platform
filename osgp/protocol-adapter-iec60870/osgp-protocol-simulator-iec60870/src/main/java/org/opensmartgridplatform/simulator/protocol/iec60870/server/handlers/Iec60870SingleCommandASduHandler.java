package org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.Iec60870ASduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Iec60870SingleCommandASduHandler extends Iec60870ASduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870SingleCommandASduHandler.class);

    public Iec60870SingleCommandASduHandler() {
        super(LOGGER, TypeId.C_SC_NA_1);
    }

    @Override
    public void accept(final Connection connection, final ASdu aSdu) throws IOException {
        throw new RuntimeException("Not implemented yet");
    }

}
