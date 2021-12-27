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

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttDeviceFactory {

  private final MqttClientDefaults mqttClientDefaults;

  @Autowired
  public MqttDeviceFactory(final MqttClientDefaults mqttClientDefaults) {
    this.mqttClientDefaults = mqttClientDefaults;
  }

  public MqttDevice create(final MessageMetadata messageMetadata) {
    final MqttDevice device = new MqttDevice(messageMetadata.getDeviceIdentification());
    device.setHost(messageMetadata.getIpAddress());
    device.setPort(this.mqttClientDefaults.getDefaultPort());
    device.setTopics(this.mqttClientDefaults.getDefaultTopics());
    device.setQos(this.mqttClientDefaults.getDefaultQos());

    return device;
  }
}
