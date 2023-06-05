// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.opensmartgridplatform.iec60870.Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("light_measurement_device")
class Iec60870UpdateInformationObjectTest {

  @Autowired private Iec60870Server iec60870Server;

  @Mock private Connection connection;

  private Map<Integer, InformationElement[][]> processImageBeforeUpdate;

  private static boolean ON = true;
  private static final IeSinglePointWithQuality EXPECTED =
      new IeSinglePointWithQuality(ON, false, false, false, false);

  @BeforeEach
  void setup() {
    this.processImageBeforeUpdate = this.iec60870Server.getProcessImage();
  }

  /**
   * informationObjectAddress 42 is in application-light_measurement_device.properties with value
   * false, we will test updating that address
   */
  @Test
  void testUpdateExistingInformationObject() {

    final int informationObjectAddress = 42;

    this.iec60870Server.updateInformationObject(
        informationObjectAddress, SINGLE_POINT_INFORMATION_WITH_QUALITY, ON);

    this.checkProcessImage(informationObjectAddress, this.iec60870Server.getProcessImage());
  }

  private void checkProcessImage(
      final int informationObjectAddress, final Map<Integer, InformationElement[][]> processImage) {
    for (final int address : processImage.keySet()) {
      final InformationElement value = processImage.get(address)[0][0];
      if (address == informationObjectAddress) {

        // check for new value
        assertThat(value).usingRecursiveComparison().isEqualTo(EXPECTED);
      } else {

        // check that the value didn't change
        assertThat(value)
            .usingRecursiveComparison()
            .isEqualTo(this.processImageBeforeUpdate.get(address)[0][0]);
      }
    }
  }

  /**
   * informationObjectAddress 2 is not in application-light_measurement_device.properties, we will
   * test updating that address
   */
  @Test
  void testAddInformationObject() {

    final int informationObjectAddress = 2;

    this.iec60870Server.updateInformationObject(
        informationObjectAddress, SINGLE_POINT_INFORMATION_WITH_QUALITY, ON);

    this.checkProcessImage(informationObjectAddress, this.iec60870Server.getProcessImage());
  }

  @Test
  void testSendEvent() throws IOException {

    this.registerConnection();

    this.iec60870Server.updateInformationObject(1, SINGLE_POINT_INFORMATION_WITH_QUALITY, ON);

    // check if an event was sent
    verify(this.connection)
        .send(
            argThat(
                new AsduTypeArgumentMatcher(ASduType.M_SP_TB_1, CauseOfTransmission.SPONTANEOUS)));
  }

  @Test
  void testDontSendEventWhenValueDidntChange() throws IOException {

    this.registerConnection();

    this.iec60870Server.updateInformationObject(127, SINGLE_POINT_INFORMATION_WITH_QUALITY, ON);

    // check that no event was sent
    verify(this.connection, never())
        .send(
            argThat(
                new AsduTypeArgumentMatcher(ASduType.M_SP_TB_1, CauseOfTransmission.SPONTANEOUS)));
  }

  private void registerConnection() throws IOException {
    final Iec60870ServerEventListener eventListener =
        this.iec60870Server.getIec60870ServerEventListener();
    eventListener.connectionIndication(this.connection);
  }
}
