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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Properties;
import java.util.UUID;
import javax.net.ssl.TrustManagerFactory;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.config.MqttConstants;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClientAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClientAdapter.class);

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

  public void connect() throws GeneralSecurityException {
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
      throws GeneralSecurityException {
    if (mqttProperties == null || mqttProperties.isEmpty()) {
      return null;
    }
    return MqttClientSslConfig.builder()
        .trustManagerFactory(getTruststoreFactory(mqttProperties))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(final Properties mqttProperties)
      throws GeneralSecurityException {

    final String trustStoreType =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME);
    final String trustStorePath =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME);
    final String trustStorePW =
        mqttProperties.getProperty(MqttConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME);

    KeyStore trustStore;
    if (Files.exists(Paths.get(trustStorePath))) {
      LOGGER.info("Load external truststore from path: {}", trustStorePath);
      trustStore = loadTrustStoreFromExternalPath(trustStoreType, trustStorePath, trustStorePW);
    } else {
      LOGGER.info("Load default truststore from classpath: {}", trustStorePath);
      trustStore = loadTrustStoreFromClassPath(trustStoreType, trustStorePath, trustStorePW);
    }

    final TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    return tmf;
  }

  private static KeyStore loadTrustStoreFromExternalPath(
      final String trustStoreType, final String trustStorePath, final String trustStorePW)
      throws GeneralSecurityException {

    try (InputStream in = new FileInputStream(trustStorePath)) {
      final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
      trustStore.load(in, trustStorePW.toCharArray());
      return trustStore;
    } catch (final Exception e) {
      throw new GeneralSecurityException("Failed loading keystore from external path.", e);
    }
  }

  private static KeyStore loadTrustStoreFromClassPath(
      final String trustStoreType, final String trustStorePath, final String trustStorePW)
      throws GeneralSecurityException {

    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStorePath)) {
      final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
      if (in == null) {
        throw new FileNotFoundException(trustStorePath);
      }
      trustStore.load(in, trustStorePW.toCharArray());
      return trustStore;
    } catch (final Exception e) {
      throw new GeneralSecurityException("Failed loading keystore from classpath.", e);
    }
  }
}
