/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class MqttClientAdapterFactoryTest {

  private MqttClientAdapterFactory instance;

  private MqttClient mqttClient;

  @Mock private MqttClientDefaults mqttClientDefaults;
  @Mock private MqttClientSslConfig mqttClientSslConfig;

  @Mock private MockedStatic<MqttClient> staticMqttClient;
  @Mock private Mqtt3AsyncClient mqtt3AsyncClient;

  @Test
  void testCreateUsingExistingMqttClient() {
    // Arrange
    final MqttDevice mqttDevice = mock(MqttDevice.class);
    final MessageMetadata messageMetaData = mock(MessageMetadata.class);
    final MqttClientEventHandler mqttClientEventHandler = mock(MqttClientEventHandler.class);

    this.mqttClient = mock(MqttClient.class);
    when(this.mqttClient.getMqtt3AsyncClient()).thenReturn(this.mqtt3AsyncClient);

    this.instance =
        new MqttClientAdapterFactory(
            this.mqttClient, this.mqttClientDefaults, this.mqttClientSslConfig);

    // Act
    final MqttClientAdapter actual =
        this.instance.create(mqttDevice, messageMetaData, mqttClientEventHandler);

    // Assert
    assertThat(actual).isNotNull();
    assertThat(actual.getDevice()).isEqualTo(mqttDevice);
    assertThat(actual.getMessageMetadata()).isEqualTo(messageMetaData);
  }

  @Test
  void testCreateWithoutExistingMqttClient() {
    // Arrange
    this.mqttClient = null;
    this.instance =
        new MqttClientAdapterFactory(
            this.mqttClient, this.mqttClientDefaults, this.mqttClientSslConfig);

    final MqttDevice mqttDevice = mock(MqttDevice.class);
    final MessageMetadata messageMetaData = mock(MessageMetadata.class);
    final MqttClientEventHandler mqttClientEventHandler = mock(MqttClientEventHandler.class);

    // Act
    final MqttClientAdapter actual =
        this.instance.create(mqttDevice, messageMetaData, mqttClientEventHandler);

    // Assert
    assertThat(actual.getDevice()).isEqualTo(mqttDevice);
    assertThat(actual.getMessageMetadata()).isEqualTo(messageMetaData);
  }
}
