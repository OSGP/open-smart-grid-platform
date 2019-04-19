package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestInfo;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;

public interface ClientConnectionService {
    /**
     * Get a connection.
     *
     * @param requestInfo
     *            The {@link RequestInfo} instance.
     * @return A {@link ClientConnection} instance.
     * @throws ConnectionFailureException
     */
    ClientConnection getConnection(RequestInfo requestInfo) throws ConnectionFailureException;

    /**
     * Close all connections.
     */
    void closeAllConnections();
}
