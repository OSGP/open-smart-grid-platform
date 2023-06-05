// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms;

import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.jms.core.JmsTemplate;

public class BaseResponseMessageSender {
  /**
   * Send a response message to the web service adapter using a custom time to live.
   *
   * @param jmsTemplate The jms template used for sending the message
   * @param responseMessage The response message to send.
   * @param timeToLive The custom time to live value in milliseconds.
   */
  protected void send(
      final JmsTemplate jmsTemplate,
      final ResponseMessage responseMessage,
      final Long timeToLive,
      final String messageType) {

    // Keep the original time to live from configuration.
    final Long originalTimeToLive = jmsTemplate.getTimeToLive();
    if (timeToLive != null) {
      // Set the custom time to live.
      jmsTemplate.setTimeToLive(timeToLive);
    }

    jmsTemplate.send(
        (final Session session) -> {
          final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
          responseMessage
              .messageMetadata()
              .builder()
              .withMessageType(messageType)
              .build()
              .applyTo(objectMessage);
          objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
          if (responseMessage.getOsgpException() != null) {
            objectMessage.setStringProperty(
                Constants.DESCRIPTION, responseMessage.getOsgpException().getMessage());
          }
          return objectMessage;
        });

    if (timeToLive != null) {
      // Restore the time to live from the configuration.
      jmsTemplate.setTimeToLive(originalTimeToLive);
    }
  }
}
