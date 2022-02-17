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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class PublishedMessage {

  private final String topic;
  private final byte[] payload;

  public PublishedMessage(final String topic, final String payload) {
    this(topic, payload.getBytes(StandardCharsets.UTF_8));
  }

  public PublishedMessage(final String topic, final byte[] payload) {
    this.topic = Objects.requireNonNull(topic);
    this.payload = payload.clone();
  }

  public String topic() {
    return this.topic;
  }

  public byte[] payload() {
    return this.payload.clone();
  }

  public String payloadAsText() {
    return new String(this.payload, StandardCharsets.UTF_8);
  }

  @Override
  public String toString() {
    return String.format("[%s] %s", this.topic, this.payloadAsText());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PublishedMessage)) {
      return false;
    }
    final PublishedMessage other = (PublishedMessage) obj;
    return this.topic.equals(other.topic) && Arrays.equals(this.payload, other.payload);
  }

  @Override
  public int hashCode() {
    return 31 * this.topic.hashCode() + Arrays.hashCode(this.payload);
  }
}
