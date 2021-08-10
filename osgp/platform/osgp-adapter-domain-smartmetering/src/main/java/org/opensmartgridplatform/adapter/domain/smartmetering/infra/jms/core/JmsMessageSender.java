/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core;

import java.io.Serializable;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.jms.core.JmsTemplate;

/** Generic JMS Message Sender */
public class JmsMessageSender {

  private final JmsTemplate jmsTemplate;

  public JmsMessageSender(final JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  /**
   * Sends an object message over the configured jmsTemplate.
   *
   * @param payload the payload of the message
   * @param messageMetadata Metadata to be set as message properties
   */
  public void send(final Serializable payload, final MessageMetadata messageMetadata) {
    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage = session.createObjectMessage(payload);
          messageMetadata.applyTo(objectMessage);
          return objectMessage;
        });
  }
}
