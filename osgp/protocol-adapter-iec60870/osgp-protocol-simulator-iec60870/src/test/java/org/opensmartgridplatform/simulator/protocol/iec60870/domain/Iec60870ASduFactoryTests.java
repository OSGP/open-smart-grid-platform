/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class Iec60870ASduFactoryTests {

    private final Iec60870ASduFactory iec60870ASduFactory = new Iec60870ASduFactory();

    @Test
    public void shouldCreateInterrogationCommand() {
        // Arrange
        final ASdu expected = new ASdu(ASduType.C_IC_NA_1, false, CauseOfTransmission.ACTIVATION, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = this.iec60870ASduFactory.createInterrogationCommandAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void shouldCreateInterrogationCommandResponse() {
        // Arrange
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        // @formatter:off
        final ASdu expected = new ASdu(ASduType.M_ME_TF_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                new InformationObject[] {
                        new InformationObject(9127,
                                new InformationElement[][] { { new IeShortFloat(10.0f),
                                        new IeQuality(false, false, false, false, false), new IeTime56(timestamp) } }),
                        new InformationObject(9128, new InformationElement[][] { { new IeShortFloat(20.5f),
                                new IeQuality(false, false, false, false, false), new IeTime56(timestamp) } }) });
        // @formatter:on

        // Act
        final ASdu actual = this.iec60870ASduFactory.createInterrogationCommandResponseAsdu(timestamp);

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void shouldCreateSingleCommand() {
        // Arrange
        final ASdu expected = new ASdu(ASduType.C_SC_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = this.iec60870ASduFactory.createSingleCommandAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
