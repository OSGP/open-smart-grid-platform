/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("light_measurement_device")
class LightMeasurementDeviceAsduFactoryTest {

    @Autowired
    private Iec60870AsduFactory iec60870AsduFactory;

    @Test
    void shouldCreateInterrogationCommandResponse() {
        // Arrange
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        final InformationObject[] expectedInformationObjects = new InformationObject[4];
        expectedInformationObjects[0] = new InformationObject(42, this.createInformationElement(false));
        expectedInformationObjects[1] = new InformationObject(78, this.createInformationElement(false));
        expectedInformationObjects[2] = new InformationObject(127, this.createInformationElement(true));
        expectedInformationObjects[3] = new InformationObject(95, this.createInformationElement(false));
        final ASdu expected = new ASdu(ASduType.M_SP_NA_1, false, CauseOfTransmission.INTERROGATED_BY_STATION, false,
                false, 0, 1, expectedInformationObjects);

        // Act
        final ASdu actual = this.iec60870AsduFactory.createInterrogationCommandResponseAsdu();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private InformationElement[][] createInformationElement(final boolean on) {
        return new InformationElement[][] { { new IeSinglePointWithQuality(on, false, false, false, false) } };
    }

}
