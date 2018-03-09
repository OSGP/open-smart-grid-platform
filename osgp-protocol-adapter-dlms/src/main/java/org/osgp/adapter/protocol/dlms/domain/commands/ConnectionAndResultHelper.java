/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public class ConnectionAndResultHelper {
    /**
     * @param conn
     * @param getParameter
     * @return
     * @throws ProtocolAdapterException
     */
    public DataObject getValidatedResultData(final DlmsConnectionHolder conn, final AttributeAddress getParameter)
            throws ProtocolAdapterException {
        GetResult getResult = null;
        try {
            getResult = conn.getConnection().get(getParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException("No GetResult received while retrieving M-Bus encryption key status.");
        }

        final DataObject dataObject = getResult.getResultData();
        if (!dataObject.isNumber()) {
            throw new ProtocolAdapterException("Received unexpected result data.");
        }
        return dataObject;
    }
}
