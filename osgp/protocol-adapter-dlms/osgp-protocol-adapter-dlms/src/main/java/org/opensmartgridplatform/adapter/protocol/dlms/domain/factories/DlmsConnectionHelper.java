/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for acquiring connections to DLMS devices, that takes care of details like initializing invocation
 * counters when required.
 */
@Component
public class DlmsConnectionHelper {
    private final InvocationCounterManager invocationCounterManager;
    private final DlmsConnectionFactory connectionFactory;

    @Autowired
    public DlmsConnectionHelper(final InvocationCounterManager invocationCounterManager,
            final DlmsConnectionFactory connectionFactory) {
        this.invocationCounterManager = invocationCounterManager;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Returns an open connection to the device, taking care of details like initializing the invocation counter when
     * required.
     */
    public DlmsConnectionManager createConnectionForDevice(final DlmsDevice device,
            final DlmsMessageListener messageListener) throws OsgpException {
        if (device.isHls5Active()) {
            this.invocationCounterManager.initializeInvocationCounter(device);
        }

        return this.connectionFactory.getConnection(device, messageListener);
    }
}
