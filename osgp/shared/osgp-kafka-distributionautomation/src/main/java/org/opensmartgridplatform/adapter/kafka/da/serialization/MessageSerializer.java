/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.serialization;

import com.alliander.data.scadameasurementpublishedevent.Message;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSerializer implements Serializer<Message> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageSerializer.class);

  @Override
  public byte[] serialize(final String topic, final Message data) {
    try {
      final ByteBuffer byteBuffer = data.toByteBuffer();
      final byte[] byteArray = new byte[byteBuffer.remaining()];
      byteBuffer.get(byteArray);
      return byteArray;
    } catch (final IOException e) {
      LOGGER.error(
          "Error during serializing message with message id {} for topic {}.",
          data.getMessageId(),
          topic,
          e);
      return new byte[0];
    }
  }
}
