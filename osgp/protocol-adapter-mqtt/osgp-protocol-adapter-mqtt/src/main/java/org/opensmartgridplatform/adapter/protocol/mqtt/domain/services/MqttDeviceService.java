/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.domain.services;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.factories.MqttDeviceFactory;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttDeviceService {

  private final MqttDeviceFactory factory;
  private final MqttDeviceRepository repository;

  @Autowired
  public MqttDeviceService(final MqttDeviceFactory factory, final MqttDeviceRepository repository) {
    this.factory = factory;
    this.repository = repository;
  }

  public MqttDevice getOrCreateDevice(final MessageMetadata messageMetadata) {
    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    MqttDevice device = this.repository.findByDeviceIdentification(deviceIdentification);
    if (device == null) {
      device = this.factory.create(messageMetadata);
      this.repository.save(device);
    }
    return device;
  }
}
