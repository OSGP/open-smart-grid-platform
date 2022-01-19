/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueuingMessageHandler implements MessageHandler {

  private final Queue<PublishedMessage> publishedMessages;

  public QueuingMessageHandler() {
    this(new ConcurrentLinkedQueue<>());
  }

  public QueuingMessageHandler(final Queue<PublishedMessage> publishedMessages) {
    this.publishedMessages = publishedMessages;
  }

  Queue<PublishedMessage> publishedMessages() {
    return this.publishedMessages;
  }

  @Override
  public void handlePublishedMessage(final String topic, final byte[] payload) {
    this.publishedMessages.add(new PublishedMessage(topic, payload));
  }
}
