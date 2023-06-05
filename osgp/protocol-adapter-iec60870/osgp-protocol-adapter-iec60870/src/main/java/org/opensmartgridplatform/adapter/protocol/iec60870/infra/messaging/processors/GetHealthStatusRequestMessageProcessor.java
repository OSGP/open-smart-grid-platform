// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.GeneralInterrogationService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.AbstractMessageProcessor;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing get health status requests. */
@Component
public class GetHealthStatusRequestMessageProcessor extends AbstractMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

  @Autowired private GeneralInterrogationService generalInterrogationService;

  public GetHealthStatusRequestMessageProcessor() {
    super(MessageType.GET_HEALTH_STATUS);
  }

  @Override
  public void process(
      final ClientConnection deviceConnection, final RequestMetadata requestMetadata)
      throws ProtocolAdapterException {

    final String deviceIdentification = requestMetadata.getDeviceIdentification();
    final String organisationIdentification = requestMetadata.getOrganisationIdentification();

    LOGGER.info(
        "getHealthStatus for IEC60870 device {} for organisation {}",
        deviceIdentification,
        organisationIdentification);

    try {
      this.generalInterrogationService.sendGeneralInterrogation(deviceConnection, requestMetadata);

    } catch (final IOException | RuntimeException e) {
      final String message =
          String.format("Requesting the health status for device %s failed", deviceIdentification);
      throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, message, e);
    }
  }
}
