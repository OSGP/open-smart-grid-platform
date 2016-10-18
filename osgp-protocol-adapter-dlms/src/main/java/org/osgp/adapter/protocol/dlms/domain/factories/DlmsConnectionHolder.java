/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

public class DlmsConnectionHolder {

    private final DlmsConnection dlmsConnection;
    private final DlmsMessageListener dlmsMessageListener;

    public DlmsConnectionHolder(final DlmsConnection dlmsConnection, final DlmsMessageListener dlmsMessageListener) {
        this.dlmsConnection = dlmsConnection;
        this.dlmsMessageListener = dlmsMessageListener;
    }

    public DlmsConnection getConnection() {
        return this.dlmsConnection;
    }

    public boolean hasDlmsMessageListener() {
        return this.dlmsMessageListener != null;
    }

    public DlmsMessageListener getDlmsMessageListener() {
        return this.dlmsMessageListener;
    }
}
