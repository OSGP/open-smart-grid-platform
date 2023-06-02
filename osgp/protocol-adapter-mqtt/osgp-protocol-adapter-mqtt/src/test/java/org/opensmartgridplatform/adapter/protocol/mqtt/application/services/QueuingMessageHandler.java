//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
