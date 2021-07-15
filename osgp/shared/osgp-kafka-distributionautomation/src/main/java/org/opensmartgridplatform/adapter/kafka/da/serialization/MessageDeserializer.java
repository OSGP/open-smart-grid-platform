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
import org.apache.avro.AvroRuntimeException;
import org.apache.kafka.common.serialization.Deserializer;

public class MessageDeserializer implements Deserializer<Message> {

  @Override
  public Message deserialize(final String topic, final byte[] data) {
    try {
      return Message.fromByteBuffer(ByteBuffer.wrap(data));
    } catch (final IOException e) {
      throw new AvroRuntimeException(e);
    }
  }
}
