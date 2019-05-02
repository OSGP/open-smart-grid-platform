/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;

/**
 *
 * Container for Client ASDU Handlers
 *
 */
public interface ClientAsduHandlerRegistry {

    /**
     * Get a handler.
     *
     * @param asdu
     *            The {@link ASdu} for which to find a handler.
     * @return A {@link ClientAsduHandler} instance.
     * @throws Iec60870ASduHandlerNotFoundException
     */
    ClientAsduHandler getHandler(ASdu asdu) throws Iec60870ASduHandlerNotFoundException;

    /**
     * Register a handler.
     *
     * @param typeId
     *            The {@link TypeId} of the handler.
     * @param clientAsduHandler
     *            The {@link ClientAsduHandler} instance.
     */
    void registerHandler(TypeId typeId, ClientAsduHandler clientAsduHandler);
}
