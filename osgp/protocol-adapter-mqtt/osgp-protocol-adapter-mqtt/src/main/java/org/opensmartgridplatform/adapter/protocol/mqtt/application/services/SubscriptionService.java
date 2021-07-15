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
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import java.util.Arrays;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubscriptionService implements MqttClientEventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

  private final MqttDeviceRepository mqttDeviceRepository;
  private final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  private final MqttClientAdapterFactory mqttClientAdapterFactory;

  private final MqttClientDefaults mqttClientDefaults;

  @Autowired @Nullable private MqttClient mqttClient;

  public SubscriptionService(
      final MqttDeviceRepository mqttDeviceRepository,
      final MqttClientAdapterFactory mqttClientAdapterFactory,
      final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender,
      final MqttClientDefaults mqttClientDefaults) {
    this.mqttDeviceRepository = mqttDeviceRepository;
    this.mqttClientAdapterFactory = mqttClientAdapterFactory;
    this.outboundOsgpCoreResponseMessageSender = outboundOsgpCoreResponseMessageSender;
    this.mqttClientDefaults = mqttClientDefaults;
  }

  public void subscribe(final MessageMetadata messageMetadata) {
    final MqttDevice device = this.getOrCreateDevice(messageMetadata);
    final MqttClientAdapter mqttClientAdapter =
        this.mqttClientAdapterFactory.create(device, messageMetadata, this);

    if (this.mqttClient == null) {
      mqttClientAdapter.connect();
    } else {
      this.subscribeUsingExistingConnection(device, mqttClientAdapter);
    }
  }

  private MqttDevice getOrCreateDevice(final MessageMetadata messageMetadata) {
    MqttDevice device =
        this.mqttDeviceRepository.findByDeviceIdentification(
            messageMetadata.getDeviceIdentification());
    if (device == null) {
      device = new MqttDevice(messageMetadata.getDeviceIdentification());
      device.setHost(messageMetadata.getIpAddress());
      device.setPort(this.mqttClientDefaults.getDefaultPort());
      device.setTopics(this.mqttClientDefaults.getDefaultTopics());
      device.setQos(this.mqttClientDefaults.getDefaultQos());
      this.mqttDeviceRepository.save(device);
    }
    return device;
  }

  private void subscribeUsingExistingConnection(
      final MqttDevice device, final MqttClientAdapter mqttClientAdapter) {
    final MqttQos qos = this.getQosOrDefault(device);
    final String[] topics = this.getTopicsForDevice(device);
    Arrays.stream(topics)
        .forEach(
            topic ->
                mqttClientAdapter.subscribe(this.mqttClient.getMqtt3AsyncClient(), topic, qos));
  }

  @Override
  public void onConnect(
      final MqttClientAdapter mqttClientAdapter,
      final Mqtt3ConnAck ack,
      final Throwable throwable) {
    if (throwable == null) {
      this.onConnectSuccess(mqttClientAdapter, ack);
    } else {
      LOG.info(
          "Client connect failed for device:{}",
          mqttClientAdapter.getMessageMetadata().getDeviceIdentification(),
          throwable);
    }
  }

  private void onConnectSuccess(final MqttClientAdapter mqttClientAdapter, final Mqtt3ConnAck ack) {
    LOG.info(
        "Client connected for device:{} ack:{}",
        mqttClientAdapter.getMessageMetadata().getDeviceIdentification(),
        ack.getType());
    final MqttDevice device = mqttClientAdapter.getDevice();
    final MqttQos qos = this.getQosOrDefault(device);
    final String[] topics = this.getTopicsForDevice(device);
    Arrays.stream(topics).forEach(topic -> mqttClientAdapter.subscribe(topic, qos));
  }

  private String[] getTopicsForDevice(final MqttDevice device) {
    return device.getTopics().split(",");
  }

  private MqttQos getQosOrDefault(final MqttDevice device) {
    MqttQos mqttQos;
    try {
      mqttQos = MqttQos.valueOf(device.getQos());
    } catch (final IllegalArgumentException | NullPointerException e) {
      LOG.warn(String.format("Illegal or missing QoS value %s, using default", device.getQos()), e);
      device.setQos(this.mqttClientDefaults.getDefaultQos());
      mqttQos = MqttQos.valueOf(device.getQos());
    }
    return mqttQos;
  }

  @Override
  public void onSubscribe(
      final MqttClientAdapter mqttClientAdapter,
      final Mqtt3SubAck subAck,
      final Throwable throwable) {
    final MessageMetadata messageMetadata = mqttClientAdapter.getMessageMetadata();
    if (throwable == null) {
      LOG.info(
          "Client subscribed for device:{} suback:{}",
          messageMetadata.getDeviceIdentification(),
          subAck.getType());
    } else {
      LOG.info(
          "Client subscription for device:{} failed",
          messageMetadata.getDeviceIdentification(),
          throwable);
    }
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
        new ProtocolResponseMessage.Builder()
            .deviceMessageMetadata(new DeviceMessageMetadata(messageMetadata))
            .domain(messageMetadata.getDomain())
            .domainVersion(messageMetadata.getDomainVersion())
            .dataObject(payload)
            .result(ResponseMessageResultType.OK)
            .build();
    this.outboundOsgpCoreResponseMessageSender.send(responseMessage);
  }
}
