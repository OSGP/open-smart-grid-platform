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
import java.util.Properties;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.config.MqttConstants;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging.OutboundOsgpCoreResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubscriptionService implements MqttClientEventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

  private final MqttDeviceRepository mqttDeviceRepository;
  private final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender;
  private final MqttClientAdapterFactory mqttClientAdapterFactory;

  private final Properties mqttClientProperties;

  public SubscriptionService(
      final MqttDeviceRepository mqttDeviceRepository,
      final MqttClientAdapterFactory mqttClientAdapterFactory,
      final OutboundOsgpCoreResponseMessageSender outboundOsgpCoreResponseMessageSender,
      final Properties mqttClientProperties) {
    this.mqttDeviceRepository = mqttDeviceRepository;
    this.mqttClientAdapterFactory = mqttClientAdapterFactory;
    this.outboundOsgpCoreResponseMessageSender = outboundOsgpCoreResponseMessageSender;
    this.mqttClientProperties = mqttClientProperties;
  }

  public void subscribe(final MessageMetadata messageMetadata) throws Exception {
    final MqttDevice device = this.getOrCreateDevice(messageMetadata);
    final MqttClientAdapter mqttClientAdapter =
        this.mqttClientAdapterFactory.create(device, messageMetadata, this);
    mqttClientAdapter.connect();
  }

  private MqttDevice getOrCreateDevice(final MessageMetadata messageMetadata) {
    MqttDevice device =
        this.mqttDeviceRepository.findByDeviceIdentification(
            messageMetadata.getDeviceIdentification());
    if (device == null) {
      device = new MqttDevice(messageMetadata.getDeviceIdentification());
      device.setHost(messageMetadata.getIpAddress());
      device.setPort(this.getDefaultPort());
      device.setTopics(this.getDefaultTopics());
      device.setQos(this.getDefaultQos());
      this.mqttDeviceRepository.save(device);
    }
    return device;
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
    final String[] topics = device.getTopics().split(",");
    Arrays.stream(topics).forEach(topic -> mqttClientAdapter.subscribe(topic, qos));
  }

  private MqttQos getQosOrDefault(final MqttDevice device) {
    MqttQos mqttQos;
    try {
      mqttQos = MqttQos.valueOf(device.getQos());
    } catch (final IllegalArgumentException | NullPointerException e) {
      LOG.warn(String.format("Illegal or missing QoS value %s, using default", device.getQos()), e);
      device.setQos(this.getDefaultQos());
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

  private int getDefaultPort() {
    return Integer.valueOf(
        this.mqttClientProperties.getProperty(MqttConstants.DEFAULT_PORT_PROPERTY_NAME));
  }

  private String getDefaultQos() {
    return this.mqttClientProperties.getProperty(MqttConstants.DEFAULT_QOS_PROPERTY_NAME);
  }

  private String getDefaultTopics() {
    return this.mqttClientProperties.getProperty(MqttConstants.DEFAULT_TOPICS_PROPERTY_NAME);
  }
}
