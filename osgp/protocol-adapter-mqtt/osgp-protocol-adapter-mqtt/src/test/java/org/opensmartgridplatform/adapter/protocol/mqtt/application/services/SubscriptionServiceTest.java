/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.services.MqttDeviceService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

  @InjectMocks private SubscriptionService instance;

  @Mock private MqttDeviceService mqttDeviceService;
  @Mock private MqttClientAdapterFactory mqttClientAdapterFactory;
  @Mock private OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;

  @Mock private MqttClientAdapter mqttClientAdapter;
  @Mock private MqttDevice mqttDevice;
  @Mock private Logger logger;

  @Captor private ArgumentCaptor<ProtocolResponseMessage> protocolResponseMessageCaptor;

  private MessageMetadata messageMetadata;

  @BeforeEach
  public void setUp() throws Exception {
    this.setupMessageMetadata();
    this.setupLogger();

    lenient().when(this.mqttClientAdapter.getMessageMetadata()).thenReturn(this.messageMetadata);
  }

  @Test
  void subscribe() {
    // Arrange
    when(this.mqttDeviceService.getOrCreateDevice(this.messageMetadata))
        .thenReturn(this.mqttDevice);
    when(this.mqttClientAdapterFactory.create(this.mqttDevice, this.messageMetadata, this.instance))
        .thenReturn(this.mqttClientAdapter);

    // Act
    this.instance.subscribe(this.messageMetadata);

    // Assert
    verify(this.mqttClientAdapter).subscribe();
  }

  @Test
  void onConnectSuccess() {
    // Arrange
    final Mqtt3ConnAck ack = spy(Mqtt3ConnAck.class);
    when(ack.getReturnCode()).thenReturn(Mqtt3ConnAckReturnCode.SUCCESS);

    final Throwable throwable = null;

    // Act
    this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

    // Assert
    verify(this.mqttClientAdapter).subscribe();
    verify(this.logger).info(anyString(), anyString());
  }

  @Test
  void onConnectError() {
    // Arrange
    final Mqtt3ConnAck ack = mock(Mqtt3ConnAck.class);
    final Throwable throwable = mock(Throwable.class);

    // Act
    this.instance.onConnect(this.mqttClientAdapter, ack, throwable);

    // Assert
    verify(this.logger).warn(anyString(), (Object[]) any());
  }

  @Test
  void onSubscribe() {
    // Arrange
    final Mqtt3SubAck subAck = mock(Mqtt3SubAck.class);
    final Throwable throwable = null;

    // Act
    this.instance.onSubscribe(
        this.mqttClientAdapter, "test-topic", MqttQos.AT_LEAST_ONCE, subAck, throwable);

    // Assert
    verify(this.logger).info(anyString(), (Object[]) any());
  }

  @Test
  void onSubscribeError() {
    // Arrange
    final Mqtt3SubAck subAck = mock(Mqtt3SubAck.class);
    final Throwable throwable = mock(Throwable.class);

    // Act
    this.instance.onSubscribe(
        this.mqttClientAdapter, "test-topic", MqttQos.AT_LEAST_ONCE, subAck, throwable);

    // Assert
    verify(this.logger).warn(anyString(), (Object[]) any());
  }

  @Test
  void onReceive() {
    // Arrange
    final String payload = "12345";
    final byte[] bytes = payload.getBytes();

    // Act
    this.instance.onReceive(this.mqttClientAdapter, bytes);

    // Assert
    verify(this.outboundOsgpCoreResponseMessageSender)
        .send(this.protocolResponseMessageCaptor.capture());
    final ProtocolResponseMessage protocolResponseMessage =
        this.protocolResponseMessageCaptor.getValue();

    verifyMessageMetadata(this.messageMetadata, protocolResponseMessage);

    assertEquals(payload, protocolResponseMessage.getDataObject());
    assertEquals(ResponseMessageResultType.OK, protocolResponseMessage.getResult());
  }

  static void setFinalStatic(final Field field, final Object newValue) throws Exception {
    field.setAccessible(true);
    final Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    field.set(null, newValue);
  }

  private void setupLogger() throws Exception {
    setFinalStatic(SubscriptionService.class.getDeclaredField("LOG"), this.logger);
    ReflectionTestUtils.setField(
        this.instance, SubscriptionService.class, "LOG", this.logger, Logger.class);
  }

  private void setupMessageMetadata() {
    this.messageMetadata =
        new MessageMetadata.Builder()
            .withCorrelationUid("test-correlation-uuid")
            .withDeviceIdentification("test-device-id")
            .withDomain("test-domain")
            .withDomainVersion("test-domain-version")
            .withMessagePriority(2345)
            .withMessageType(MessageType.GET_DATA.name())
            .withOrganisationIdentification("test-organisation-id")
            .withBypassRetry(true)
            .build();
  }

  private static void verifyMessageMetadata(
      final MessageMetadata metadata, final ProtocolResponseMessage message) {
    assertThat(message.getCorrelationUid()).isEqualTo(metadata.getCorrelationUid());
    assertThat(message.getDeviceIdentification()).isEqualTo(metadata.getDeviceIdentification());
    assertThat(message.getDomain()).isEqualTo(metadata.getDomain());
    assertThat(message.getDomainVersion()).isEqualTo(metadata.getDomainVersion());
    assertThat(message.getMessagePriority()).isEqualTo(metadata.getMessagePriority());
    assertThat(message.getMessageType()).isEqualTo(metadata.getMessageType());
    assertThat(message.getOrganisationIdentification())
        .isEqualTo(metadata.getOrganisationIdentification());
    assertThat(message.bypassRetry()).isEqualTo(metadata.isBypassRetry());
    assertThat(message.isScheduled()).isFalse();
  }
}
