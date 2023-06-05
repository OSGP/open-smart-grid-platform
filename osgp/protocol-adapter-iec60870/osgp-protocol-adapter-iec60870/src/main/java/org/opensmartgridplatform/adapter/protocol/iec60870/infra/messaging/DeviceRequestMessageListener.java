// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.NotSupportedException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "protocolIec60870InboundOsgpCoreRequestsMessageListener")
public class DeviceRequestMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListener.class);

  @Autowired
  @Qualifier("protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap")
  private MessageProcessorMap messageProcessorMap;

  @Autowired
  @Qualifier("protocolIec60870OutboundOsgpCoreResponsesMessageSender")
  private DeviceResponseMessageSender deviceResponseMessageSender;

  @Autowired private PendingRequestsQueue pendingRequestsQueue;

  @Override
  public void onMessage(final Message message) {

    ObjectMessage objectMessage = null;
    MessageMetadata messageMetadata = null;
    try {
      objectMessage = (ObjectMessage) message;
      messageMetadata = MessageMetadata.fromMessage(objectMessage);
    } catch (final Exception e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read JMS message instance, giving up.", e);
      return;
    }

    final String correlationUid = messageMetadata.getCorrelationUid();
    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    try {
      LOGGER.info(
          "Received message [correlationUid={}, messageType={}, messagePriority={}] for device {}",
          correlationUid,
          messageMetadata.getMessageType(),
          messageMetadata.getMessagePriority(),
          deviceIdentification);

      this.pendingRequestsQueue.enqueue(deviceIdentification, correlationUid);

      final MessageProcessor processor =
          this.messageProcessorMap.getMessageProcessor(objectMessage);

      processor.processMessage(objectMessage);

    } catch (final IllegalArgumentException | JMSException e) {

      this.pendingRequestsQueue.remove(deviceIdentification, correlationUid);
      LOGGER.error("Unexpected exception for message [correlationUid={}]", correlationUid, e);
      this.sendNotSupportedException(objectMessage, messageMetadata);
    }
  }

  private void sendNotSupportedException(
      final ObjectMessage objectMessage, final MessageMetadata messageMetadata) {

    try {
      final Exception exception =
          new NotSupportedException(
              ComponentType.PROTOCOL_IEC60870, messageMetadata.getMessageType());
      final FunctionalException osgpException =
          new FunctionalException(
              FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
              ComponentType.PROTOCOL_IEC60870,
              exception);

      final ProtocolResponseMessage protocolResponseMessage =
          ProtocolResponseMessage.newBuilder()
              .messageMetadata(messageMetadata.builder().withScheduled(false).build())
              .result(ResponseMessageResultType.NOT_OK)
              .osgpException(osgpException)
              .dataObject(objectMessage.getObject())
              .build();

      this.deviceResponseMessageSender.send(protocolResponseMessage);

    } catch (final Exception e) {
      LOGGER.error("Unexpected error during sendException(ObjectMessage, Exception)", e);
    }
  }
}
