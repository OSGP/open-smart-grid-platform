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
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;
import java.util.UUID;
import javax.net.ssl.TrustManagerFactory;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.config.MqttConstants;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class MqttClientAdapter {

  private final MqttDevice device;
  private final MessageMetadata messageMetadata;
  private final Properties mqttClientProperties;
  private final MqttClientEventHandler mqttClientEventHandler;
  private Mqtt3AsyncClient client;

  public MqttClientAdapter(
      final MqttDevice device,
      final MessageMetadata messageMetadata,
      final Properties mqttClientProperties,
      final MqttClientEventHandler mqttClientEventHandler) {
    this.device = device;
    this.messageMetadata = messageMetadata;
    this.mqttClientProperties = mqttClientProperties;
    this.mqttClientEventHandler = mqttClientEventHandler;
  }

  public void connect() throws Exception {
    final String id = UUID.randomUUID().toString();
    this.client =
        Mqtt3Client.builder()
            .identifier(id)
            .serverHost(this.device.getHost())
            .serverPort(this.device.getPort())
            .sslConfig(getSslConfig(this.mqttClientProperties))
            .buildAsync();
    this.client
        .connectWith()
        .send()
        .whenComplete(
            (ack, throwable) -> this.mqttClientEventHandler.onConnect(this, ack, throwable));
  }

  public void subscribe(final String topic, final MqttQos qos) {
    this.client
        .subscribeWith()
        .topicFilter(topic)
        .qos(qos)
        .callback(this::publishPayload)
        .send()
        .whenComplete(
            (subAck, throwable) ->
                this.mqttClientEventHandler.onSubscribe(this, subAck, throwable));
  }

  private void publishPayload(final Mqtt3Publish publish) {
    publish
        .getPayload()
        .ifPresent(
            byteBuffer -> this.mqttClientEventHandler.onReceive(this, publish.getPayloadAsBytes()));
  }

  public MqttDevice getDevice() {
    return this.device;
  }

  public MessageMetadata getMessageMetadata() {
    return this.messageMetadata;
  }

  private static MqttClientSslConfig getSslConfig(final Properties mqttProperties)
      throws Exception {
    if (mqttProperties == null || mqttProperties.isEmpty()) {
      return null;
    }
    return MqttClientSslConfig.builder()
        .trustManagerFactory(getTruststoreFactory(mqttProperties))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(final Properties mqttProperties)
      throws Exception {

    final String trustStoreType =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME);
    final String trustStorePath =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME);
    final String trustStorePW =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME);

    final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
    InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStorePath);
    if (in == null) {
      in = new FileInputStream(trustStorePath);
    }
    trustStore.load(in, trustStorePW.toCharArray());

    final TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    return tmf;
  }
}
