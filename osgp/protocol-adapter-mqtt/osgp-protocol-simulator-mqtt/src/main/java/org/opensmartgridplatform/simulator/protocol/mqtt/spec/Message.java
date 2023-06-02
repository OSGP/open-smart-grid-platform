//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.nio.charset.StandardCharsets;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
  private String topic;
  private byte[] payload;
  private long pauseMillis;

  private Boolean zip;

  public Message() {}

  public Message(final String topic, final byte[] payload, final long pauseMillis) {
    this(topic, payload, pauseMillis, false);
  }

  public Message(
      final String topic, final byte[] payload, final long pauseMillis, final Boolean zip) {
    this.topic = topic;
    this.payload = payload;
    this.pauseMillis = pauseMillis;
    this.zip = zip;
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

  public boolean getZip() {
    return Boolean.TRUE.equals(this.zip);
  }
}
