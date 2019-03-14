/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeScaledValue;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class Iec60870ASduFactoryTests {

    private Iec60870ASduFactory iec60870ASduFactory = new Iec60870ASduFactory();

    @Test
    public void shouldCreateInterrogationCommand() {
        // Arrange
        final ASdu expected = new ASdu(TypeId.C_IC_NA_1, false, CauseOfTransmission.ACTIVATION, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = this.iec60870ASduFactory.createInterrogationCommandASdu();

        // Assert
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void shouldCreateInterrogationCommandResponse() {
        // Arrange
        final ASdu expected = new ASdu(TypeId.M_ME_NB_1, true, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                new InformationObject[] { new InformationObject(1,
                        new InformationElement[][] {
                                { new IeScaledValue(-32768), new IeQuality(true, true, true, true, true) },
                                { new IeScaledValue(10), new IeQuality(true, true, true, true, true) },
                                { new IeScaledValue(-5), new IeQuality(true, true, true, true, true) } }) });

        // Act
        final ASdu actual = this.iec60870ASduFactory.createInterrogationCommandResponseASdu();

        // Assert
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void shouldCreateSingleCommand() {
        // Arrange
        final ASdu expected = new ASdu(TypeId.C_SC_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = this.iec60870ASduFactory.createSingleCommandASdu();

        // Assert
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}
