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
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN_VERSION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_ORGANISATION_IDENTIFICATION;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.AsduFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;

/**
 * org.mockito.exceptions.base.MockitoException: 
 * Mockito cannot mock this class: class org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCacheImpl.
 * Mockito can only mock non-private & non-final classes.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ClientConnectionEventListenerTest {

    private final static String DEVICE_IDENTIFICATION = "TEST-DEVICE-1";

    private ClientConnectionEventListener clientConnectionEventListener;
    private ResponseMetadata responseMetadata;

    @Mock
    private ClientConnectionCache connectionCache;

    @Mock
    private ClientAsduHandlerRegistry asduHandlerRegistry;

    @Mock
    private ClientAsduHandler asduHandler;

    @Before
    public void setup() {
        this.responseMetadata = new ResponseMetadata.Builder().withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withOrganisationIdentification(DEFAULT_ORGANISATION_IDENTIFICATION)
                .withDomainInfo(new DomainInfo(DEFAULT_DOMAIN, DEFAULT_DOMAIN_VERSION))
                .withMessageType(DEFAULT_MESSAGE_TYPE).build();
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
