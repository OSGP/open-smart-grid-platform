/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
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
