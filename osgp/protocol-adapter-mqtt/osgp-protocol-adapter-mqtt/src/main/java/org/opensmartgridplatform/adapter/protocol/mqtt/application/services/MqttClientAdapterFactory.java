/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class MqttClientAdapterFactory {

  @Nullable private final MqttClient mqttClient;
  private final MqttClientDefaults mqttClientDefaults;
  @Nullable private final MqttClientSslConfig mqttClientSslConfig;

  @Autowired
  public MqttClientAdapterFactory(
      @Nullable final MqttClient mqttClient,
      final MqttClientDefaults mqttClientDefaults,
      @Nullable final MqttClientSslConfig mqttClientSslConfig) {
    this.mqttClient = mqttClient;
    this.mqttClientDefaults = mqttClientDefaults;
    this.mqttClientSslConfig = mqttClientSslConfig;
  }

  public MqttClientAdapter create(
      final MqttDevice device,
      final MessageMetadata messageMetadata,
      final MqttClientEventHandler mqttClientEventHandler) {
    return new MqttClientAdapter(
        device,
        messageMetadata,
        this.mqttClient != null ? this.mqttClient.getMqtt3AsyncClient() : null,
        this.mqttClientDefaults,
        this.mqttClientSslConfig,
        mqttClientEventHandler);
  }
}
