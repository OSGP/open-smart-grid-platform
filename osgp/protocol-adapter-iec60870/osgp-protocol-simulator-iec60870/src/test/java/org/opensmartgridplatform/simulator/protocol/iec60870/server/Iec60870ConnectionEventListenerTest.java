// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionEventListener;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation.DefaultControlledStationAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandAsduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandAsduHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class Iec60870ConnectionEventListenerTest {

  @Mock private Iec60870ConnectionRegistry iec60870ConnectionRegistry;

  @Mock private Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;

  @Mock private Iec60870InterrogationCommandAsduHandler interrogationCommandHandler;

  @Mock private Iec60870SingleCommandAsduHandler singleCommandHandler;

  @Mock private Connection connection;

  private final DefaultControlledStationAsduFactory iec60870AsduFactory =
      new DefaultControlledStationAsduFactory();

  private Iec60870ConnectionEventListener iec60870ConnectionEventListener;

  @BeforeEach
  void setup() throws IOException {
    this.iec60870ConnectionEventListener =
        new Iec60870ConnectionEventListener(
            this.connection, this.iec60870ConnectionRegistry, this.iec60870AsduHandlerRegistry);
  }

  @Test
  void interrogationCommandShouldBeHandledByInterrogationCommandHandler() throws Exception {
    // Arrange
    final ASdu asdu = this.iec60870AsduFactory.createInterrogationCommandAsdu();
    when(this.iec60870AsduHandlerRegistry.getHandler(ASduType.C_IC_NA_1))
        .thenReturn(this.interrogationCommandHandler);

    // Act
    this.iec60870ConnectionEventListener.newASdu(asdu);

    // Assert
    verify(this.interrogationCommandHandler).handleAsdu(any(Connection.class), any(ASdu.class));
  }

  @Test
  void singleCommandShouldBeHandledBySingleCommandHandler() throws Exception {
    // Arrange
    final ASdu asdu = this.iec60870AsduFactory.createSingleCommandAsdu();
    when(this.iec60870AsduHandlerRegistry.getHandler(ASduType.C_SC_NA_1))
        .thenReturn(this.singleCommandHandler);

    // Act
    this.iec60870ConnectionEventListener.newASdu(asdu);

    // Assert
    Mockito.verify(this.singleCommandHandler, Mockito.times(1)).handleAsdu(this.connection, asdu);
  }
}
