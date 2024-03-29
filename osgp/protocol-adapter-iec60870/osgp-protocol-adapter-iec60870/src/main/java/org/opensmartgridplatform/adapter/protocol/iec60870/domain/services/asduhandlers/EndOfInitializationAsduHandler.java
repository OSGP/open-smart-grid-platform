// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AbstractClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Abstract class providing an implementation for handling end of initialization ASDUs. */
@Component
public class EndOfInitializationAsduHandler extends AbstractClientAsduHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EndOfInitializationAsduHandler.class);

  public EndOfInitializationAsduHandler() {
    super(ASduType.M_EI_NA_1);
  }

  @Override
  public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
    LOGGER.info(
        "Controlled station for device {} is available after remote initialization.",
        responseMetadata.getDeviceIdentification());
  }
}
