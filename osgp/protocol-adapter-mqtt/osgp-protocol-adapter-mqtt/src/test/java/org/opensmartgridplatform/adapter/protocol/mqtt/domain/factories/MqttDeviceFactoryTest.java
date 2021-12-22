/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class MqttDeviceFactoryTest {

  private static final String DEVICE_IDENTIFICATION = "test-device-id";
  private static final String HOST = "test-host";

  private static final int DEFAULT_PORT = 8883;
  private static final String[] DEFAULT_TOPICS = {"topic1", "topic2"};
  private static final String DEFAULT_QOS = "AT_LEAST_ONCE";

  @Mock MqttClientDefaults mqttClientDefaults;

  @InjectMocks private MqttDeviceFactory instance;

  @Test
  void testCreate() {

    // Arrange
    final MessageMetadata messageMetadata = createMessageMetadata();
    this.setupMqttClientDefaultsMock();

    // Act
    final MqttDevice actual = this.instance.create(messageMetadata);

    // Assert
    assertThat(actual)
        .hasFieldOrPropertyWithValue("deviceIdentification", DEVICE_IDENTIFICATION)
        .hasFieldOrPropertyWithValue("host", HOST)
        .hasFieldOrPropertyWithValue("port", DEFAULT_PORT)
        .hasFieldOrPropertyWithValue("qos", DEFAULT_QOS)
        .hasFieldOrPropertyWithValue("topics", DEFAULT_TOPICS);
  }

  private void setupMqttClientDefaultsMock() {
    when(this.mqttClientDefaults.getDefaultPort()).thenReturn(DEFAULT_PORT);
    when(this.mqttClientDefaults.getDefaultTopics()).thenReturn(DEFAULT_TOPICS);
    when(this.mqttClientDefaults.getDefaultQos()).thenReturn(DEFAULT_QOS);
  }

  private static MessageMetadata createMessageMetadata() {
    return new MessageMetadata.Builder()
        .withIpAddress(HOST)
        .withDeviceIdentification(DEVICE_IDENTIFICATION)
        .build();
  }
}
