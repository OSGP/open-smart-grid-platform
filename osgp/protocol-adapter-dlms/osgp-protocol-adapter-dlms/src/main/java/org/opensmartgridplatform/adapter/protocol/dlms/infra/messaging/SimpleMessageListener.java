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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleMessageListener implements MessageListener {

  ArrayList<Message> msgReceived;
  AtomicBoolean result;

  SimpleMessageListener() {
    this.result = new AtomicBoolean(true);
    this.msgReceived = new ArrayList<>();
  }

  @Override
  public void onMessage(final Message msg) {
    if (!(msg instanceof TextMessage)) {
      log.warn("Received message is not a TextMessage");
      return;
    }
    try {
      final TextMessage textMessage = (TextMessage) msg;
      log.info("Received message : [" + textMessage.getText() + "]");
    } catch (final JMSException e) {
      this.result.set(false);
    }
    this.msgReceived.add(msg);
  }
}
