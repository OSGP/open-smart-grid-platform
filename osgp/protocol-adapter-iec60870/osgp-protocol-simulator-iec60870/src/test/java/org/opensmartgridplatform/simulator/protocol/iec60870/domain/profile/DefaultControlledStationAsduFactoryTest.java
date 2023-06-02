//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation.DefaultControlledStationAsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("default_controlled_station")
class DefaultControlledStationAsduFactoryTest {

  @Autowired private Iec60870AsduFactory iec60870AsduFactory;

  @Autowired private InformationElementFactory informationElementFactory;

  @Test
  void testCreateInterrogationCommandResponse() {

    // Arrange
    this.iec60870AsduFactory.initialize();

    final InformationObject[] expectedInformationObjects = new InformationObject[2];
    expectedInformationObjects[0] =
        new InformationObject(9127, this.createInformationElement(10.0f));
    expectedInformationObjects[1] =
        new InformationObject(9128, this.createInformationElement(20.5f));
    final ASdu expected =
        new ASdu(
            ASduType.M_ME_NC_1,
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

  private InformationElement[][] createInformationElement(final float value) {
    return this.informationElementFactory.createInformationElements(
        Iec60870InformationObjectType.SHORT_FLOAT, value);
  }

  @Test
  void testCreateSingleCommand() {

    // Arrange
    final DefaultControlledStationAsduFactory defaultControlledAsduFactory =
        new DefaultControlledStationAsduFactory();
    final ASdu expected =
        new ASdu(
            ASduType.C_SC_NA_1,
            false,
            CauseOfTransmission.SPONTANEOUS,
            false,
            false,
            0,
            1,
            new InformationObject[] {
              new InformationObject(
                  0,
                  this.informationElementFactory.createInformationElements(
                      Iec60870InformationObjectType.QUALIFIER_OF_INTERROGATION, 20))
            });

    // Act
    final ASdu actual = defaultControlledAsduFactory.createSingleCommandAsdu();

    // Assert
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}
