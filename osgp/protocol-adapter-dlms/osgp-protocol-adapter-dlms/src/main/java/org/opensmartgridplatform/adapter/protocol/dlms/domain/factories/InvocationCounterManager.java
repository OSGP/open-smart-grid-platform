/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Object that manages invocation counters.
 */
@Component
public class InvocationCounterManager {
    private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE = new AttributeAddress(1,
            new ObisCode(new byte[] { 0, 0, 43, 1, 0, -1 }), 2);

    private final DlmsConnectionFactory connectionFactory;
    private final DlmsHelperService dlmsHelper;
    private final DlmsDeviceRepository deviceRepository;

    @Autowired
    public InvocationCounterManager(final DlmsConnectionFactory connectionFactory, final DlmsHelperService dlmsHelper,
            final DlmsDeviceRepository deviceRepository) {
        this.connectionFactory = connectionFactory;
        this.dlmsHelper = dlmsHelper;
        this.deviceRepository = deviceRepository;
    }

    /**
     * Updates the device instance with the invocation counter value on the actual device.
     */
    public void initializeInvocationCounter(final DlmsDevice device) throws OsgpException {
        if (this.invocationCounterIsStoredOnDevice(device)) {
            this.initializeWithInvocationCounterStoredOnDevice(device);
        } else {
            // Value of invocation counter is ignored on these devices.
            device.setInvocationCounter(0);
        }
        this.deviceRepository.save(device);
    }

    private void initializeWithInvocationCounterStoredOnDevice(final DlmsDevice device) throws OsgpException {
        try (final DlmsConnectionManager connectionManager = this.connectionFactory
                .getPublicClientConnection(device, null)) {
            device.setInvocationCounter(this.getInvocationCounter(connectionManager));
        }
    }

    private boolean invocationCounterIsStoredOnDevice(final DlmsDevice device) {
        return "SMR".equals(device.getProtocol());
    }

    private int getInvocationCounter(final DlmsConnectionManager connectionManager) throws FunctionalException {
        return ((Number) this.dlmsHelper
                .getAttributeValue(connectionManager, ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE).getValue())
                .intValue();
    }
}
