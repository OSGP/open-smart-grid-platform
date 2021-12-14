/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import java.time.Duration;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DeviceKeyProcessingService;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageSender;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class KeysRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private DeviceRequestMessageSender deviceRequestMessageSender;

  @Autowired private DeviceKeyProcessingService deviceKeyProcessingService;

  public KeysRequestMessageProcessor(final MessageType messageType) {
    super(messageType);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    final Serializable messageObject = message.getObject();

    log.debug(
        "Checking if not already processing key for device {} before processing {} request message",
        messageMetadata.getDeviceIdentification(),
        messageMetadata.getMessageType());

    try {

      this.deviceKeyProcessingService.startProcessing(messageMetadata.getDeviceIdentification());

      super.processMessage(message);

    } catch (final DeviceKeyProcessAlreadyRunningException exception) {

      final Duration deviceKeyProcessingTimeout =
          this.deviceKeyProcessingService.getDeviceKeyProcessingTimeout();
      log.debug(
          "Key(s) already being processed for device {}. {} request message is sent back to the queue with delay of {} seconds.",
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getMessageType(),
          deviceKeyProcessingTimeout.getSeconds());
      /*
       * The device is already handling a key changing process, send the request back to the queue to be
       * picked up again a little later by the message listener for device requests.
       */
      this.deviceRequestMessageSender.send(
          messageObject, messageMetadata, deviceKeyProcessingTimeout);

    } catch (final Exception exception) {
      this.sendErrorResponse(messageMetadata, exception, messageObject);
    } finally {
      this.deviceKeyProcessingService.stopProcessing(messageMetadata.getDeviceIdentification());
    }
  }
}
