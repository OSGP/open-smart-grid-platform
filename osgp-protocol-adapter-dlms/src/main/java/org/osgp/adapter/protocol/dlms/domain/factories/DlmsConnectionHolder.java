/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.RawMessageData;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

public class DlmsConnectionHolder {

    private final DlmsConnection dlmsConnection;
    private final DlmsMessageListener dlmsMessageListener;

    private static final DlmsMessageListener DO_NOTHING_LISTENER = new DlmsMessageListener() {

        @Override
        public void messageCaptured(final RawMessageData rawMessageData) {
            // Do nothing.
        }

        @Override
        public void setMessageMetadata(final DlmsDeviceMessageMetadata messageMetadata) {
            // Do nothing.
        }

        @Override
        public void setDescription(final String description) {
            // Do nothing.
        }
    };

    public DlmsConnectionHolder(final DlmsConnection dlmsConnection, final DlmsMessageListener dlmsMessageListener) {
        this.dlmsConnection = dlmsConnection;
        if (dlmsMessageListener == null) {
            this.dlmsMessageListener = DO_NOTHING_LISTENER;
        } else {
            this.dlmsMessageListener = dlmsMessageListener;
        }
    }

    public DlmsConnectionHolder(final DlmsConnection dlmsConnection) {
        this(dlmsConnection, null);
    }

    public DlmsConnection getConnection() {
        return this.dlmsConnection;
    }

    public boolean hasDlmsMessageListener() {
        return DO_NOTHING_LISTENER != this.dlmsMessageListener;
    }

    public DlmsMessageListener getDlmsMessageListener() {
        return this.dlmsMessageListener;
    }
}
