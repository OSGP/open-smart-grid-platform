/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.AsduFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class ClientConnectionEventListenerTest {

    private final static String DEVICE_IDENTIFICATION = "TEST-DEVICE-1";

    private ClientConnectionEventListener clientConnectionEventListener;
    private ResponseMetadata responseMetadata = null;

    @Mock
    private ClientConnectionCache connectionCache;

    @Mock
    private ClientAsduHandlerRegistry asduHandlerRegistry;

    @Mock
    private ClientAsduHandler asduHandler;

    @Before
    public void setUp() throws Exception {
        this.clientConnectionEventListener = new ClientConnectionEventListener(DEVICE_IDENTIFICATION,
                this.connectionCache, this.asduHandlerRegistry, this.responseMetadata);
    }

    @Test
    public void shouldHandleAsduWhenNewAsduIsReceived() throws Iec60870ASduHandlerNotFoundException {
        // Arrange
        final ASdu asdu = AsduFactory.ofType(TypeId.C_IC_NA_1);
        when(this.asduHandlerRegistry.getHandler(asdu)).thenReturn(this.asduHandler);

        // Act
        this.clientConnectionEventListener.newASdu(asdu);

        // Assert
        verify(this.asduHandler).handleAsdu(asdu, this.responseMetadata);
    }

    @Test
    public void shouldRemoveConnectionFromCacheWhenConnectionIsClosed() {
        // Arrange
        final IOException e = new IOException();

        // Act
        this.clientConnectionEventListener.connectionClosed(e);

        // Assert
        verify(this.connectionCache).removeConnection(DEVICE_IDENTIFICATION);
    }

}
