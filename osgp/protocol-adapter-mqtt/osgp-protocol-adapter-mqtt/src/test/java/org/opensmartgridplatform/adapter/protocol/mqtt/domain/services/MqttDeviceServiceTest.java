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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.factories.MqttDeviceFactory;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class MqttDeviceServiceTest {

  private static final String DEVICE_IDENTIFICATION = "dvc-001";

  @Mock private MessageMetadata messageMetadata;
  @Mock private MqttDevice mqttDevice;
  @Mock private MqttDeviceFactory mqttDeviceFactory;
  @Mock private MqttDeviceRepository mqttDeviceRepository;

  @InjectMocks private MqttDeviceService instance;

  @BeforeEach
  void setup() {
    when(this.messageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
  }

  @Test
  void testGetOrCreateDeviceReturnsExistingDevice() {
    // Arrange
    when(this.mqttDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(this.mqttDevice);

    // Act
    final MqttDevice actualDevice = this.instance.getOrCreateDevice(this.messageMetadata);

    // Assert
    verify(this.mqttDeviceRepository).findByDeviceIdentification(DEVICE_IDENTIFICATION);
    verifyNoInteractions(this.mqttDeviceFactory);

    assertThat(actualDevice).isEqualTo(this.mqttDevice);
  }

  @Test
  void testGetOrCreateDeviceCreatesAndReturnsNewDevice() {
    // Arrange
    when(this.mqttDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);
    when(this.mqttDeviceFactory.create(this.messageMetadata)).thenReturn(this.mqttDevice);

    // Act
    final MqttDevice actualDevice = this.instance.getOrCreateDevice(this.messageMetadata);

    // Assert
    verify(this.mqttDeviceRepository).save(this.mqttDevice);
    verify(this.mqttDeviceFactory).create(this.messageMetadata);

    assertThat(actualDevice).isEqualTo(this.mqttDevice);
  }
}
