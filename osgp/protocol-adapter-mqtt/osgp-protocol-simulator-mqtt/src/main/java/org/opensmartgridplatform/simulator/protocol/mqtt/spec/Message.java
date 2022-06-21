/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

import java.nio.charset.StandardCharsets;

public class Message {

  private String topic;
  private byte[] payload;
  private long pauseMillis;

  public Message() {}

  public Message(final String topic, final byte[] payload, final long pauseMillis) {
    this.topic = topic;
    this.payload = payload;
    this.pauseMillis = pauseMillis;
  }

  public String getTopic() {
    return this.topic;
  }

  public byte[] getPayload() {
    return this.payload;
  }

  public void setPayload(final byte[] payload) {
    this.payload = payload;
  }

  public void setPayload(final String payload) {
    this.payload = payload.getBytes(StandardCharsets.UTF_8);
  }

  public long getPauseMillis() {
    return this.pauseMillis;
  }
}
