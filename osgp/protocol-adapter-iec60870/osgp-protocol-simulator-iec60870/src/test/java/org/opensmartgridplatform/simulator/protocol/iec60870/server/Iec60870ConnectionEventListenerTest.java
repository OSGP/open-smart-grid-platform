/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionEventListener;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile.DefaultControlledStationAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandASduHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class Iec60870ConnectionEventListenerTest {
    @Mock
    private Iec60870ConnectionRegistry iec60870ConnectionRegistry;

    @Mock
    private Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;

    @Mock
    private Iec60870InterrogationCommandASduHandler interrogationCommandHandler;

    @Mock
    private Iec60870SingleCommandASduHandler singleCommandHandler;

    @Mock
    private Connection connection;

    private final DefaultControlledStationAsduFactory iec60870aSduFactory = new DefaultControlledStationAsduFactory();

    private Iec60870ConnectionEventListener iec60870ConnectionEventListener;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.iec60870ConnectionEventListener = new Iec60870ConnectionEventListener(this.connection,
                this.iec60870ConnectionRegistry, this.iec60870ASduHandlerRegistry);
    }

    @Test
    public void interrogationCommandShouldBeHandledByInterrogationCommandHandler() throws Exception {
        // Arrange
        final ASdu aSdu = this.iec60870aSduFactory.createInterrogationCommandAsdu();
        when(this.iec60870ASduHandlerRegistry.getHandler(ASduType.C_IC_NA_1))
                .thenReturn(this.interrogationCommandHandler);

        // Act
        this.iec60870ConnectionEventListener.newASdu(aSdu);

        // Assert
        verify(this.interrogationCommandHandler).handleASdu(any(Connection.class), any(ASdu.class));
    }

    @Test
    public void singleCommandShouldBeHandledBySingleCommandHandler() throws Exception {
        // Arrange
        final ASdu aSdu = this.iec60870aSduFactory.createSingleCommandAsdu();
        when(this.iec60870ASduHandlerRegistry.getHandler(ASduType.C_SC_NA_1)).thenReturn(this.singleCommandHandler);

        // Act
        this.iec60870ConnectionEventListener.newASdu(aSdu);

        // Assert
        Mockito.verify(this.singleCommandHandler, Mockito.times(1)).handleASdu(this.connection, aSdu);
    }

}
