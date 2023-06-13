// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.UnknownMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// This class should fetch request messages from incoming requests queue of OSGP Core.
@Component(value = "domainTariffSwitchingInboundOsgpCoreRequestsMessageListener")
public class OsgpCoreRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OsgpCoreRequestMessageListener.class);

  @Autowired
  @Qualifier("domainTariffSwitchingInboundOsgpCoreRequestsMessageProcessor")
  private OsgpCoreRequestMessageProcessor messageProcessor;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message");

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String messageType = objectMessage.getJMSType();
      final RequestMessage requestMessage = (RequestMessage) objectMessage.getObject();

      this.messageProcessor.processMessage(requestMessage, messageType);

    } catch (final JMSException e) {
      // Can't read message.
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
    } catch (final UnknownMessageTypeException e) {
      // Don't know this message.
      LOGGER.error("UnknownMessageTypeException", e);
    }
  }
}
