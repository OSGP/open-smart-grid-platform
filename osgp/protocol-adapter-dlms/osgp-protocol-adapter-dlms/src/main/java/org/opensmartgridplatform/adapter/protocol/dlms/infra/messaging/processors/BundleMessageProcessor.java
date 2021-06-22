/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.BundleService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing find events request messages */
@Component
public class BundleMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private BundleService bundleService;

  public BundleMessageProcessor() {
    super(MessageType.HANDLE_BUNDLED_ACTIONS);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws ProtocolAdapterException {

    this.assertRequestObjectType(BundleMessagesRequestDto.class, requestObject);
    final BundleMessagesRequestDto bundleMessagesRequest = (BundleMessagesRequestDto) requestObject;

    return this.bundleService.callExecutors(conn, device, bundleMessagesRequest);
  }

  /*
   * Overriding sendResponseMessage to check OK message for retryable FaultResponses.
   */
  @Override
  protected void sendResponseMessage(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final Exception exception,
      final DeviceResponseMessageSender responseMessageSender,
      final Serializable responseObject) {

    ResponseMessageResultType checkedResult = result;

    if (result.equals(ResponseMessageResultType.OK)) {

      final BundleMessagesRequestDto response = (BundleMessagesRequestDto) responseObject;
      final boolean shouldRetry =
          response.getActionList().stream()
              .map(a -> a.getResponse())
              .filter(r -> r != null)
              .filter(r -> r instanceof FaultResponseDto)
              .anyMatch(f -> ((FaultResponseDto) f).isRetryable());

      if (shouldRetry) {
        checkedResult = ResponseMessageResultType.NOT_OK;
      }
    }

    super.sendResponseMessage(
        messageMetadata, checkedResult, exception, responseMessageSender, responseObject);
  }
}
