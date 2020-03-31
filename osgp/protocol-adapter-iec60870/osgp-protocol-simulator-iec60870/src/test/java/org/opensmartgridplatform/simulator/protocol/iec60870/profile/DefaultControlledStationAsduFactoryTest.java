/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile.DefaultControlledStationAsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("default_controlled_station")
class DefaultControlledStationAsduFactoryTest {

    @Autowired
    private Iec60870AsduFactory iec60870AsduFactory;

    @Test
    void testCreateInterrogationCommand() {

        // Arrange
        final ASdu expected = new ASdu(ASduType.C_IC_NA_1, false, CauseOfTransmission.ACTIVATION, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = this.iec60870AsduFactory.createInterrogationCommandAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testCreateInterrogationCommandResponse() {

        // Arrange
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        final InformationObject[] expectedInformationObjects = new InformationObject[2];
        expectedInformationObjects[0] = new InformationObject(9127, this.createInformationElement(10.0f, timestamp));
        expectedInformationObjects[1] = new InformationObject(9128, this.createInformationElement(20.5f, timestamp));
        final ASdu expected = new ASdu(ASduType.M_ME_TF_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                expectedInformationObjects);

        // Act
        final ASdu actual = this.iec60870AsduFactory.createInterrogationCommandResponseAsdu(timestamp);

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private InformationElement[][] createInformationElement(final float value, final long timestamp) {
        return new InformationElement[][] { { new IeShortFloat(value), new IeQuality(false, false, false, false, false),
                new IeTime56(timestamp) } };
    }

    @Test
    void testCreateSingleCommand() {

        // Arrange
        final DefaultControlledStationAsduFactory defaultControlledAsduFactory = new DefaultControlledStationAsduFactory();
        final ASdu expected = new ASdu(ASduType.C_SC_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0, 1,
                new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { new IeQualifierOfInterrogation(20) } }) });

        // Act
        final ASdu actual = defaultControlledAsduFactory.createSingleCommandAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
