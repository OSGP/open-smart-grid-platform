/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.Optional;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ConnectResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870ConnectResponseService implements ConnectResponseService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec60870ConnectResponseService.class);

  @Autowired private PendingRequestsQueue pendingRequestsQueue;

  @Autowired private DeviceResponseMessageSender deviceResponseMessageSender;

  @Override
  public void handleConnectResponse(final ResponseMetadata responseMetadata) {
    LOGGER.info(
        "Handle connect response called for device {}", responseMetadata.getDeviceIdentification());

    final Optional<String> pendingRequestCorrelationUid =
        this.pendingRequestsQueue.dequeue(responseMetadata.getDeviceIdentification());

    if (pendingRequestCorrelationUid.isPresent()) {
      LOGGER.info("Pending request correlationUid is present, sending connect response.");
      this.sendConnectResponse(responseMetadata);
    } else {
      LOGGER.info(
          "No pending request correlationUid found for device {}.",
          responseMetadata.getDeviceIdentification());
    }
  }

  private void sendConnectResponse(final ResponseMetadata responseMetadata) {
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(responseMetadata.asMessageMetadata())
            .result(ResponseMessageResultType.OK)
            .build();
    this.deviceResponseMessageSender.send(responseMessage);
  }
}
