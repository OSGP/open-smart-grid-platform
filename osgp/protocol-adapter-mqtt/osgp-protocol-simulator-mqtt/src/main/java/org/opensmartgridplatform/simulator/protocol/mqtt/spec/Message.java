/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class Message {

  private final Pattern zipTopicPattern =
      Pattern.compile("\\Amsr/[^/]++/(?:data/.++)\\Z", Pattern.CASE_INSENSITIVE);

  private String topic;
  private String payload;
  private long pauseMillis;

  public Message() {}

  public Message(final String topic, final String payload, final long pauseMillis) {
    this.topic = topic;
    this.payload = payload;
    this.pauseMillis = pauseMillis;
  }

  public String getTopic() {
    return this.topic;
  }

  public String getPayload() {
    return this.payload;
  }

  public long getPauseMillis() {
    return this.pauseMillis;
  }

  public byte[] getPayloadAsBytes() {
    if (this.zipTopicPattern.matcher(this.topic).matches()) {
      return this.zippedPayload();
    }
    return this.getPayload().getBytes(StandardCharsets.UTF_8);
  }

  private byte[] zippedPayload() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
      gzipOutputStream.write(this.payload.getBytes(StandardCharsets.UTF_8));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    return outputStream.toByteArray();
  }
}
