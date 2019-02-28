package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870ASduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandASduHandler;

public class Iec60870ConnectionEventListenerTests {

    @Mock
    Iec60870ASduHandlerMap iec60870ASduHandlerMap;

    @Mock
    Iec60870InterrogationCommandASduHandler interrogationCommandHandler;

    @Mock
    Iec60870SingleCommandASduHandler singleCommandHandler;

    @Mock
    Connection connection;

    Iec60870ASduFactory iec60870aSduFactory = new Iec60870ASduFactory();

    Iec60870ConnectionEventListener iec60870ConnectionEventListener;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.iec60870ConnectionEventListener = new Iec60870ConnectionEventListener(this.connection, 1,
                this.iec60870ASduHandlerMap);
    }

    @Test
    public void interrogationCommandShouldBeHandledByInterrogationCommandHandler() throws IOException {
        // Arrange
        final ASdu aSdu = this.iec60870aSduFactory.createInterrogationCommandASdu();
        //
        when(this.iec60870ASduHandlerMap.getHandler(TypeId.C_IC_NA_1)).thenReturn(this.interrogationCommandHandler);

        // Act
        this.iec60870ConnectionEventListener.newASdu(aSdu);

        // Assert
        verify(this.interrogationCommandHandler).accept(any(Connection.class), any(ASdu.class));
    }

    @Test
    public void singleCommandShouldBeHandledBySingleCommandHandler() throws IOException {
        // Arrange
        final ASdu aSdu = this.iec60870aSduFactory.createSingleCommandASdu();

        when(this.iec60870ASduHandlerMap.getHandler(TypeId.C_SC_NA_1)).thenReturn(this.singleCommandHandler);

        // Act
        this.iec60870ConnectionEventListener.newASdu(aSdu);

        // Assert
        Mockito.verify(this.singleCommandHandler, Mockito.times(1)).accept(this.connection, aSdu);
    }

}
