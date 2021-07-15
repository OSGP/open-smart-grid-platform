/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class MqttClientAdapterFactory {

  public MqttClientAdapter create(
      final MqttDevice device,
      final MessageMetadata messageMetadata,
      final MqttClientEventHandler mqttClientEventHandler) {
    return new MqttClientAdapter(device, messageMetadata, mqttClientEventHandler);
  }
}
