//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.TimeZone;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.AsduFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AsduSteps {

  @Autowired private ClientConnectionCache clientConnectionCacheSpy;

  @Autowired private ConnectionSteps connectionSteps;

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSteps.class);

  @When("I receive an ASDU of type {string} from the IEC60870 device")
  public void whenIReceiveAsduOfType(final String asduType) {
    LOGGER.debug("When I receive an ASDU of type {}", asduType);

    final ASdu asdu = AsduFactory.ofType(ASduType.valueOf(asduType));
    this.connectionSteps.getConnectionEventListener().newASdu(asdu);
  }

  @When("a light measurement event occurs for address {int} with value {string}")
  public void aLightMeasurementOccursForAddress(
      final int informationObjectAddress, final String informationElementValue) {

    final boolean on = "ON".equalsIgnoreCase(informationElementValue);
    final InformationObject[] informationObjects = {
      new InformationObject(
          informationObjectAddress,
          new IeSinglePointWithQuality(on, false, false, false, false),
          new IeTime56(System.currentTimeMillis(), TimeZone.getDefault(), false))
    };
    final ASdu asdu =
        new ASdu(
            ASduType.M_SP_TB_1,
            false,
            CauseOfTransmission.SPONTANEOUS,
            false,
            false,
            0,
            1,
            informationObjects);
    this.connectionSteps.getConnectionEventListener().newASdu(asdu);
  }

  @Then("I should send a general interrogation command to device {string}")
  public void thenIShouldSendAGeneralInterrogationCommandToDevice(final String deviceIdentification)
      throws Exception {
    LOGGER.debug(
        "Then I should send a general interrogation command to device {}", deviceIdentification);

    final DeviceConnection deviceConnection =
        (DeviceConnection) this.clientConnectionCacheSpy.getConnection(deviceIdentification);
    final Connection connectionMock = deviceConnection.getConnection();
    verify(connectionMock)
        .interrogation(
            eq(0), eq(CauseOfTransmission.ACTIVATION), any(IeQualifierOfInterrogation.class));
  }
}
