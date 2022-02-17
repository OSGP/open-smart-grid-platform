/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects;

import com.hivemq.client.mqtt.datatypes.MqttQos;

public class MqttClientDefaults {
  private final String clientId;
  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final String qos;
  private final String[] topics;
  private final boolean cleanSession;
  private final int keepAlive;

  private MqttClientDefaults(final Builder builder) {
    this.clientId = builder.clientId;
    this.host = builder.host;
    this.port = builder.port;
    this.username = builder.username;
    this.password = builder.password;
    this.qos = builder.qos;
    this.topics = builder.topics;
    this.cleanSession = builder.cleanSession;
    this.keepAlive = builder.keepAlive;
  }

  public String getDefaultClientId() {
    return this.clientId;
  }

  public String getDefaultHost() {
    return this.host;
  }

  public int getDefaultPort() {
    return this.port;
  }

  public String getDefaultUsername() {
    return this.username;
  }

  public String getDefaultPassword() {
    return this.password;
  }

  public String getDefaultQos() {
    return this.qos;
  }

  public String[] getDefaultTopics() {
    return this.topics;
  }

  public boolean isDefaultCleanSession() {
    return this.cleanSession;
  }

  public int getDefaultKeepAlive() {
    return this.keepAlive;
  }

  public static class Builder {
    private String clientId;
    private String host = "localhost";
    private int port = 1883;
    private String username;
    private String password;
    private String qos = MqttQos.AT_LEAST_ONCE.name();
    private String[] topics = {"+/measurement"};
    private boolean cleanSession = true;
    private int keepAlive = 60;

    public Builder withClientId(final String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder withHost(final String host) {
      this.host = host;
      return this;
    }

    public Builder withPort(final int port) {
      this.port = port;
      return this;
    }

    public Builder withUsername(final String username) {
      this.username = username;
      return this;
    }

    public Builder withPassword(final String password) {
      this.password = password;
      return this;
    }

    public Builder withQos(final String qos) {
      this.qos = qos;
      return this;
    }

    public Builder withTopics(final String[] topics) {
      this.topics = topics;
      return this;
    }

    public Builder withCleanSession(final boolean cleanSession) {
      this.cleanSession = cleanSession;
      return this;
    }

    public Builder withKeepAlive(final int keepAlive) {
      this.keepAlive = keepAlive;
      return this;
    }

    public MqttClientDefaults build() {
      return new MqttClientDefaults(this);
    }
  }
}
