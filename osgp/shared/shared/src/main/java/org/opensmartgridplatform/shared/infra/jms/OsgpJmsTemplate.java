// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import org.springframework.jms.core.JmsTemplate;

/**
 * Custom implementation of {@link JmsTemplate} in order to utilize the JMS priority of JMS messages
 * instead of the global message priority of the template.
 */
public class OsgpJmsTemplate extends JmsTemplate {

  /**
   * Different implementation in order to use message priority of JMS message. Delivery delay
   * implementation has been removed, see {@link JmsTemplate#doSend(MessageProducer, Message)}.
   */
  @Override
  protected void doSend(final MessageProducer producer, final Message message) throws JMSException {
    if (this.isExplicitQosEnabled()) {
      producer.send(
          message, this.getDeliveryMode(), message.getJMSPriority(), this.getTimeToLive());
    } else {
      producer.send(message);
    }
  }
}
