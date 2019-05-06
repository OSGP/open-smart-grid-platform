/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;

public interface ClientConnectionService {
    /**
     * Gets a connection.
     *
     * @param requestMetadata
     *            The {@link RequestMetadata} instance.
     * @return A {@link ClientConnection} instance.
     * @throws ConnectionFailureException
     */
    ClientConnection getConnection(RequestMetadata requestMetadata) throws ConnectionFailureException;

    /**
     * Close all connections.
     */
    void closeAllConnections();
}
