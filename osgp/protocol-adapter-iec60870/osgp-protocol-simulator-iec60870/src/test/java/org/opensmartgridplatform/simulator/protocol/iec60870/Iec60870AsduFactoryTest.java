/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;

/**
 * Class for testing the default methods of Iec60870AsduFactory
 *
 */
public class Iec60870AsduFactoryTest {

    private final Iec60870AsduFactory iec60870AsduFactory = new AsduFactory();

    @Test
    void testCreateInterrogationCommand() {

        // Arrange
        final ASdu expected = this.getAsdu(CauseOfTransmission.ACTIVATION_CON);

        // Act
        final ASdu actual = this.iec60870AsduFactory.createInterrogationCommandAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testCreateActivationTerminationResponse() {

        // Arrange
        final ASdu expected = this.getAsdu(CauseOfTransmission.ACTIVATION_TERMINATION);

        // Act
        final ASdu actual = this.iec60870AsduFactory.createActivationTerminationResponseAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private ASdu getAsdu(final CauseOfTransmission causeOfTransmission) {
        return new ASdu(ASduType.C_IC_NA_1, false, causeOfTransmission, false, false, 0, 1,
                this.getInformationObjects());
    }

    private InformationObject[] getInformationObjects() {
        return new InformationObject[] {
                new InformationObject(0, new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) };
    }

    private class AsduFactory implements Iec60870AsduFactory {

        @Override
        public ASdu createInterrogationCommandResponseAsdu() {
            // we only test the default methods in this class
            return null;
        }

    }
}
