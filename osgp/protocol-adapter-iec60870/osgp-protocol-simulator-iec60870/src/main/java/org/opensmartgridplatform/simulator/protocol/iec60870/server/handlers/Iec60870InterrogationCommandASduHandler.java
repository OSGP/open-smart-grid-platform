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
    Iec60870ASduFactory iec60870aSduFactory;

    public Iec60870InterrogationCommandASduHandler() {
        super(LOGGER, TYPE_ID);
    }

    @Override
    public void accept(final Connection connection, final ASdu aSdu) throws IOException {
        LOGGER.info("Received interrogation command. Sending confirmation.");
        connection.sendConfirmation(aSdu);

        LOGGER.info("Processing interrogation command. Sending scaled measured values.");
        connection.send(this.iec60870aSduFactory.createInterrogationCommandResponseASdu());
    }
}
