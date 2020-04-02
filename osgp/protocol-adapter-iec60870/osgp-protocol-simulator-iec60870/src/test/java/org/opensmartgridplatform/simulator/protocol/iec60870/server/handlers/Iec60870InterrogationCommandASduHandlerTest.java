/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;

@ExtendWith(MockitoExtension.class)
class Iec60870InterrogationCommandASduHandlerTest {

    @Mock
    private Iec60870AsduFactory iec60870AsduFactory;

    @Mock
    private Connection connection;

    @Test
    void testSendingOrder() throws IOException {

        // Arrange
        doNothing().when(this.connection).sendConfirmation(any(ASdu.class));
        final ASdu responseAsdu = this.getAsdu(ASduType.M_SP_NA_1);
        when(this.iec60870AsduFactory.createInterrogationCommandResponseAsdu()).thenReturn(responseAsdu);
        final ASdu terminationAsdu = this.getAsdu(ASduType.C_IC_NA_1);
        when(this.iec60870AsduFactory.createActivationTerminationResponseAsdu()).thenReturn(terminationAsdu);
        doNothing().when(this.connection).send(any(ASdu.class));

        final InOrder inOrder = inOrder(this.connection);

        // Act
        final Iec60870InterrogationCommandASduHandler interrogationCommandHandler = new Iec60870InterrogationCommandASduHandler(
                this.iec60870AsduFactory);
        interrogationCommandHandler.handleASdu(this.connection, responseAsdu);

        // Assert
        inOrder.verify(this.connection).sendConfirmation(any(ASdu.class));
        inOrder.verify(this.connection).send(argThat(new AsduTypeArgumentMatcher(ASduType.M_SP_NA_1)));
        inOrder.verify(this.connection).send(argThat(new AsduTypeArgumentMatcher(ASduType.C_IC_NA_1)));

    }

    private ASdu getAsdu(final ASduType asduType) {
        return new ASdu(asduType, false, CauseOfTransmission.ACTIVATION, false, false, 0, 1,
                this.getInformationObjects());
    }

    private InformationObject[] getInformationObjects() {
        return new InformationObject[] {
                new InformationObject(0, new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) };
    }

    private class AsduTypeArgumentMatcher implements ArgumentMatcher<ASdu> {

        private final ASduType type;

        public AsduTypeArgumentMatcher(final ASduType type) {
            this.type = type;
        }

        @Override
        public boolean matches(final ASdu argument) {
            return argument.getTypeIdentification() == this.type;
        }

    }
}
