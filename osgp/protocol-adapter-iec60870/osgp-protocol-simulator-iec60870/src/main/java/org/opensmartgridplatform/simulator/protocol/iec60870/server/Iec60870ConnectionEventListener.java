package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870ConnectionEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ConnectionEventListener.class);

    private Iec60870ASduHandlerMap iec60870ASduHandlerMap;
    private final Connection connection;
    private final int connectionId;

    public Iec60870ConnectionEventListener(final Connection connection, final int connectionId,
            final Iec60870ASduHandlerMap iec60870aSduHandlerMap) {
        this.connection = connection;
        this.connectionId = connectionId;
        this.iec60870ASduHandlerMap = iec60870aSduHandlerMap;
    }

    @Override
    public void newASdu(final ASdu aSdu) {
        Objects.requireNonNull(aSdu);
        try {
            final TypeId typeId = aSdu.getTypeIdentification();
            final Iec60870ASduHandler aSduHandler = this.iec60870ASduHandlerMap.getHandler(typeId);

            Objects.requireNonNull(aSduHandler,
                    String.format("Unknown request received, no handler available for ASdu: %s", aSdu.toString()));

            aSduHandler.accept(this.connection, aSdu);

        } catch (final NullPointerException npe) {
            LOGGER.error(npe.getMessage());
        } catch (final Exception e) {
            if (e instanceof EOFException) {
                LOGGER.error("Connection closed on connection ({}): {}.", this.connectionId, e.getMessage());
            } else {
                LOGGER.error("Exception occurred on connection ({}): {}.", this.connectionId, e.getMessage());
            }
        }

    }

    @Override
    public void connectionClosed(final IOException e) {
        LOGGER.info("Connection ({}) closed. {}", this.connectionId, e.getMessage());
    }
}
