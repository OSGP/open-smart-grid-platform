/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionInfo;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;

public interface Client {

    /**
     * Connect to a device.
     *
     * @param connectionInfo
     *            The {@link ConnectionInfo} instance.
     * @param asduListener
     *            The {@link ConnectionEventListener} instance.
     * @return A {@link ClientConnection} instance.
     * @throws ConnectionFailureException
     *
     */
    ClientConnection connect(ConnectionInfo connectionInfo, ConnectionEventListener asduListener)
            throws ConnectionFailureException;

    /**
     * Disconnect from the device.
     *
     * @param clientConnection
     *            The {@link ClientConnection} instance.
     */
    void disconnect(ClientConnection clientConnection);
}
