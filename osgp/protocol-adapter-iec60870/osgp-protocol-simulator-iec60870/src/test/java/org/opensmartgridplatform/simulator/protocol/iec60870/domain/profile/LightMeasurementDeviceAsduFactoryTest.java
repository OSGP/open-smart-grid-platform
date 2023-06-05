// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.iec60870.factory.InformationElementFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("light_measurement_device")
class LightMeasurementDeviceAsduFactoryTest {

  @Autowired private Iec60870AsduFactory iec60870AsduFactory;

  @Autowired private InformationElementFactory informationElementFactory;

  @Test
  void shouldCreateInterrogationCommandResponse() {

    // Arrange
    this.iec60870AsduFactory.initialize();

    final InformationObject[] expectedInformationObjects = new InformationObject[4];
    expectedInformationObjects[0] = new InformationObject(42, this.createInformationElement(false));
    expectedInformationObjects[1] = new InformationObject(78, this.createInformationElement(false));
    expectedInformationObjects[2] = new InformationObject(127, this.createInformationElement(true));
    expectedInformationObjects[3] = new InformationObject(95, this.createInformationElement(false));
    final ASdu expected =
        new ASdu(
            ASduType.M_SP_NA_1,
            false,
            CauseOfTransmission.INTERROGATED_BY_STATION,
            false,
            false,
            0,
            1,
            expectedInformationObjects);

    // Act
    final ASdu actual = this.iec60870AsduFactory.createInterrogationCommandResponseAsdu();

    // Assert
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  private InformationElement[][] createInformationElement(final boolean on) {
    return this.informationElementFactory.createInformationElements(
        Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY, on);
  }
}
