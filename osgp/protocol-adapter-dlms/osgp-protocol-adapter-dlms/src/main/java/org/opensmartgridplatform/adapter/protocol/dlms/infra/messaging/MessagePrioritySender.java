/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.util.Random;
import javax.jms.TextMessage;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessagePrioritySender {

  private final JmsTemplate priorityJmsTemplate;
  private final DefaultMessageListenerContainer priorityMessageListenerContainer;

  public MessagePrioritySender(
      final JmsTemplate priorityJmsTemplate,
      final DefaultMessageListenerContainer priorityMessageListenerContainer)
      throws SSLException {
    this.priorityJmsTemplate = priorityJmsTemplate;
    this.priorityMessageListenerContainer = priorityMessageListenerContainer;
  }

  public void testPriority() throws Exception {
    log.info("Message listener stop");
    this.priorityMessageListenerContainer.stop();
    log.info("Wait 5000ms");
    Thread.sleep(5000);

    final Random random = new Random();
    final int maxMessages = 1000;
    for (int i = 0; i < maxMessages; i++) {
      final int priority = random.nextInt(10);
      this.send(priority, "message nr:" + i + ", prio " + priority);
    }

    log.info("Wait 5000ms");
    Thread.sleep(5000);
    log.info("Message listener start");
    this.priorityMessageListenerContainer.start();
  }

  void send(final int messagePriority, final String text) {
    this.priorityJmsTemplate.send(
        session -> {
          final TextMessage message = session.createTextMessage();
          message.setText(text);
          message.setJMSPriority(messagePriority);
          return message;
        });
    log.info("Message sent: " + text + " with priority: " + messagePriority);
  }
}
