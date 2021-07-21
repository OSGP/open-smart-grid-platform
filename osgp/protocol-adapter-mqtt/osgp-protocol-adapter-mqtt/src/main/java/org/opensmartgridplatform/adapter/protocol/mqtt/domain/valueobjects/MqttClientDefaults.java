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

public class MqttClientDefaults {
  private final String defaultHost;
  private final int defaultPort;
  private final String defaultUsername;
  private final String defaultPassword;
  private final String defaultQos;
  private final String defaultTopics;

  public MqttClientDefaults(
      final String defaultHost,
      final int defaultPort,
      final String defaultUsername,
      final String defaultPassword,
      final String defaultQos,
      final String defaultTopics) {
    this.defaultHost = defaultHost;
    this.defaultPort = defaultPort;
    this.defaultUsername = defaultUsername;
    this.defaultPassword = defaultPassword;
    this.defaultQos = defaultQos;
    this.defaultTopics = defaultTopics;
  }

  public String getDefaultHost() {
    return this.defaultHost;
  }

  public int getDefaultPort() {
    return this.defaultPort;
  }

  public String getDefaultUsername() {
    return this.defaultUsername;
  }

  public String getDefaultPassword() {
    return this.defaultPassword;
  }

  public String getDefaultQos() {
    return this.defaultQos;
  }

  public String getDefaultTopics() {
    return this.defaultTopics;
  }
}
