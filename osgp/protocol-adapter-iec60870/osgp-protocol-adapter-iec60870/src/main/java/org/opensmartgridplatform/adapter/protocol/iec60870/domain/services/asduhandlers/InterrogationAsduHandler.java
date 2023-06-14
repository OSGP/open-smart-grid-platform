// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AbstractClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ConnectResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ASDU Handler for ASDUs with type identification C_IC_NA_1:.
 *
 * <ul>
 *   <li>Interrogation Command
 * </ul>
 */
@Component
public class InterrogationAsduHandler extends AbstractClientAsduHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterrogationAsduHandler.class);

  @Autowired private ConnectResponseService connectResponseService;

  public InterrogationAsduHandler() {
    super(ASduType.C_IC_NA_1);
  }

  @Override
  public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
    LOGGER.debug(
        "Received interrogation command with cause of transmission {}.",
        asdu.getCauseOfTransmission());

    if (asdu.getCauseOfTransmission() == CauseOfTransmission.ACTIVATION_TERMINATION) {
      LOGGER.info(
          "Received activation termination, call handle connect response for device {}.",
          responseMetadata.getDeviceIdentification());
      this.connectResponseService.handleConnectResponse(responseMetadata);
    }
  }
}
