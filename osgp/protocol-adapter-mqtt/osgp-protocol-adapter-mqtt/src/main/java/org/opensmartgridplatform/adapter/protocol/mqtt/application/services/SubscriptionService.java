/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAckReturnCode;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.services.MqttDeviceService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubscriptionService implements MqttClientEventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

  private final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  private final MqttClientAdapterFactory mqttClientAdapterFactory;
  private final MqttDeviceService mqttDeviceService;

  private final Map<String, MqttClientAdapter> mqttClientAdapters = new HashMap<>();

  @Autowired
  public SubscriptionService(
      final MqttDeviceService mqttDeviceService,
      final MqttClientAdapterFactory mqttClientAdapterFactory,
      final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender) {
    this.mqttDeviceService = mqttDeviceService;
    this.mqttClientAdapterFactory = mqttClientAdapterFactory;
    this.outboundOsgpCoreResponseMessageSender = outboundOsgpCoreResponseMessageSender;
  }

  public void subscribe(final MessageMetadata messageMetadata) {
    final MqttDevice device = this.mqttDeviceService.getOrCreateDevice(messageMetadata);
    final MqttClientAdapter mqttClientAdapter =
        this.mqttClientAdapters.computeIfAbsent(
            device.getDeviceIdentification(),
            k -> this.mqttClientAdapterFactory.create(device, messageMetadata, this));

    if (mqttClientAdapter.isDisconnected()) {
      LOG.info("MQTT Client is disconnected, connecting...");
      mqttClientAdapter.connect();
    } else if (mqttClientAdapter.isWaitingForReconnect()) {
      LOG.info("MQTT Client is waiting for reconnect...");
    } else {
      LOG.info("MQTT Client is available, subscribing...");
      mqttClientAdapter.subscribe();
    }
  }

  @PreDestroy
  public void shutdown() {
    LOG.info("Disconnecting MQTT Clients...");
    for (final MqttClientAdapter mqttClientAdapter : this.mqttClientAdapters.values()) {
      mqttClientAdapter.disconnect();
    }
  }

  @Override
  public void onConnect(
      final MqttClientAdapter mqttClientAdapter,
      final Mqtt3ConnAck ack,
      final Throwable throwable) {
    if (isConnectSuccess(ack, throwable)) {
      this.onConnectSuccess(mqttClientAdapter);
    } else {
      this.onConnectFailure(mqttClientAdapter, ack, throwable);
    }
  }

  private static boolean isConnectSuccess(final Mqtt3ConnAck ack, final Throwable throwable) {
    return (throwable == null
        && (ack != null && ack.getReturnCode() == Mqtt3ConnAckReturnCode.SUCCESS));
  }

  private void onConnectSuccess(final MqttClientAdapter mqttClientAdapter) {
    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();
    LOG.info("Client connected for device: {}", deviceIdentification);
    mqttClientAdapter.subscribe();
  }

  private void onConnectFailure(
      final MqttClientAdapter mqttClientAdapter,
      final Mqtt3ConnAck ack,
      final Throwable throwable) {

    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();
    LOG.warn("Client connect failed for device: {}, ack: {}", deviceIdentification, ack, throwable);
  }

  @Override
  public void onSubscribe(
      final MqttClientAdapter mqttClientAdapter,
      final String topic,
      final MqttQos qos,
      final Mqtt3SubAck ack,
      final Throwable throwable) {

    if (this.isSubscribeSuccess(ack, throwable)) {
      this.onSubscribeSuccess(mqttClientAdapter, topic, qos, ack);
    } else {
      this.onSubscribeFailure(mqttClientAdapter, topic, qos, ack, throwable);
    }
  }

  private boolean isSubscribeSuccess(final Mqtt3SubAck ack, final Throwable throwable) {
    return (throwable == null
        && (ack != null
            && ack.getReturnCodes().stream().noneMatch(rc -> rc == Mqtt3SubAckReturnCode.FAILURE)));
  }

  private void onSubscribeSuccess(
      final MqttClientAdapter mqttClientAdapter,
      final String topic,
      final MqttQos qos,
      final Mqtt3SubAck ack) {

    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();

    LOG.info(
        "Client subscribed for device: {}, topic: {}, qos: {},  ack: {}",
        deviceIdentification,
        topic,
        qos,
        ack);
  }

  private void onSubscribeFailure(
      final MqttClientAdapter mqttClientAdapter,
      final String topic,
      final MqttQos qos,
      final Mqtt3SubAck ack,
      final Throwable throwable) {

    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();

    LOG.warn(
        "Client subscription failed for device: {}, topic: {}, qos: {}, ack: {}",
        deviceIdentification,
        topic,
        qos,
        ack,
        throwable);
  }

  @Override
  public void onReceive(final MqttClientAdapter mqttClientAdapter, final byte[] payloadAsBytes) {
    final String payload = new String(payloadAsBytes);
    final MessageMetadata messageMetadata = mqttClientAdapter.getMessageMetadata();
    LOG.info(
        "Client for device:{} received payload:{}",
        messageMetadata.getDeviceIdentification(),
        payload);
    final ResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .dataObject(payload)
            .result(ResponseMessageResultType.OK)
            .build();
    this.outboundOsgpCoreResponseMessageSender.send(responseMessage);
  }

  @Override
  public void onUnsubscribe(
      final MqttClientAdapter mqttClientAdapter, final String topic, final Throwable throwable) {
    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();
    if (throwable == null) {
      LOG.info("Client unsubscribed for device:{}, topic: {}", deviceIdentification, topic);
    } else {
      LOG.info(
          "Client unsubscribe failed for device:{}, topic: {}",
          deviceIdentification,
          topic,
          throwable);
    }
  }

  @Override
  public void onDisconnect(final MqttClientAdapter mqttClientAdapter, final Throwable throwable) {
    final String deviceIdentification =
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification();
    if (throwable == null) {
      LOG.info("Client disconnected for device:{}", deviceIdentification);
      this.mqttClientAdapters.remove(deviceIdentification);
    } else {
      LOG.info("Client disconnect failed for device:{}", deviceIdentification, throwable);
    }
  }
}
