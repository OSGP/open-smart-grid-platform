package org.opensmartgridplatform.iec60870;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Server.class);

    private Iec60870ServerEventListener iec60870ServerEventListener;
    private Server server;

    public Iec60870Server(final Iec60870ServerEventListener iec60870ServerEventListener) {
        this.iec60870ServerEventListener = iec60870ServerEventListener;
    }

    public void start() {
        this.server = new Server.Builder().build();

        try {
            LOGGER.info("Starting IEC60870 Server.");
            this.server.start(this.iec60870ServerEventListener);
            LOGGER.info("Started IEC60870 Server.");
        } catch (final IOException e) {
            LOGGER.error("Exception occurred while starting IEC60870 server.", e);
        }
    }

    public void stop() {
        LOGGER.info("Stopping IEC60870 Server.");
        this.server.stop();
        LOGGER.info("Stopped IEC60870 Server.");
    }

    public Iec60870ServerEventListener getIec60870ServerEventListener() {
        return this.iec60870ServerEventListener;
    }

    public void pushAsdu(final ASdu asdu) {

    }
}
