// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.NotSupportedException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
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

@Component(value = "protocolOslpInboundOsgpCoreRequestsMessageListener")
public class DeviceRequestMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListener.class);

  @Autowired
  @Qualifier("protocolOslpDeviceRequestMessageProcessorMap")
  private MessageProcessorMap oslpRequestMessageProcessorMap;

  @Autowired private DeviceResponseMessageSender deviceResponseMessageSender;

  @Override
  public void onMessage(final Message message) {
    final ObjectMessage objectMessage = (ObjectMessage) message;
    String messageType = null;
    final int messagePriority;

    try {
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      LOGGER.info(
          "Received message of type: {} with message priority: {}", messageType, messagePriority);
      final MessageProcessor processor =
          this.oslpRequestMessageProcessorMap.getMessageProcessor(objectMessage);
      processor.processMessage(objectMessage);
    } catch (final JMSException ex) {
      LOGGER.error("Unexpected JMSException during onMessage(Message)", ex);
      this.sendException(objectMessage, ex, "JMSException while processing message");
    } catch (final IllegalArgumentException e) {
      LOGGER.error("Unexpected IllegalArgumentException during onMessage(Message)", e);
      this.sendException(
          objectMessage,
          new NotSupportedException(ComponentType.PROTOCOL_OSLP, messageType),
          "Unsupported device function: " + messageType);
    }
  }

  private void sendException(
      final ObjectMessage objectMessage, final Exception exception, final String errorMessage) {
    try {
      final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
      final OsgpException osgpException =
          new OsgpException(ComponentType.PROTOCOL_OSLP, errorMessage, exception);
      final Serializable dataObject = objectMessage.getObject();

      final MessageMetadata messageMetadata =
          MessageMetadata.fromMessage(objectMessage).builder().withScheduled(false).build();
      final ProtocolResponseMessage protocolResponseMessage =
          ProtocolResponseMessage.newBuilder()
              .messageMetadata(messageMetadata)
              .result(result)
              .osgpException(osgpException)
              .dataObject(dataObject)
              .build();

      this.deviceResponseMessageSender.send(protocolResponseMessage);
    } catch (final Exception e) {
      LOGGER.error("Unexpected error during sendException(ObjectMessage, Exception)", e);
    }
  }
}
