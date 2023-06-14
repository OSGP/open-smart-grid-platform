// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers;

import java.io.IOException;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870InterrogationCommandAsduHandler extends Iec60870AsduHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec60870InterrogationCommandAsduHandler.class);

  private final Iec60870AsduFactory iec60870AsduFactory;

  @Autowired
  public Iec60870InterrogationCommandAsduHandler(final Iec60870AsduFactory iec60870AsduFactory) {
    super(ASduType.C_IC_NA_1);
    this.iec60870AsduFactory = iec60870AsduFactory;
  }

  @Override
  public void handleAsdu(final Connection connection, final ASdu asdu) throws IOException {
    LOGGER.info("Received interrogation command. Sending confirmation for ASDU: {}", asdu);
    connection.sendConfirmation(asdu);

    final ASdu responseAsdu = this.iec60870AsduFactory.createInterrogationCommandResponseAsdu();
    LOGGER.info("Processing interrogation command. Sending response ASDU: {}.", responseAsdu);
    connection.send(responseAsdu);

    final ASdu terminationAsdu = this.iec60870AsduFactory.createActivationTerminationResponseAsdu();
    LOGGER.info(
        "Finished processing interrogation command. Sending termination ASDU: {}", terminationAsdu);
    connection.send(terminationAsdu);
  }
}
