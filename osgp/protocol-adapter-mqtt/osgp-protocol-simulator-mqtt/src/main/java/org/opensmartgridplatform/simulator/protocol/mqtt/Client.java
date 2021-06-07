/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HiveMQ MQQT v3 client base class.
 *
 * <p>Configuration specified by injected org.opensmartgridplatform.simulator.protocol.mqtt.Config
 */
public abstract class Client extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);

  private final UUID uuid;
  private final String host;
  private final int port;
  private final Properties mqttClientProperties;
  private volatile boolean running;
  private Mqtt3BlockingClient mqtt3BlockingClient;

  protected Client(final String host, final int port, final Properties mqttClientProperties) {
    this.host = host;
    this.port = port;
    this.mqttClientProperties = mqttClientProperties;
    this.uuid = UUID.randomUUID();
  }

  @Override
  public void run() {
    this.running = true;

    try {
      this.mqtt3BlockingClient =
          Mqtt3Client.builder()
              .identifier(this.uuid.toString())
              .serverHost(this.host)
              .serverPort(this.port)
              .sslConfig(getSslConfig(this.mqttClientProperties))
              .buildBlocking();

      final Mqtt3ConnAck ack = this.mqtt3BlockingClient.connect();
      LOG.info("Client {} received Ack {}", this.getClass().getSimpleName(), ack.getType());
      this.addShutdownHook();
      LOG.info("Client {} started", this.getClass().getSimpleName());
      this.onConnect(this.mqtt3BlockingClient);
    } catch (final Exception ex) {
      LOG.error("Exception while starting client.", ex);
      this.stopClient();
    }
  }

  private static MqttClientSslConfig getSslConfig(final Properties mqttClientProperties)
      throws GeneralSecurityException {
    if (mqttClientProperties == null || mqttClientProperties.isEmpty()) {
      return null;
    }
    return MqttClientSslConfig.builder()
        .trustManagerFactory(getTruststoreFactory(mqttClientProperties))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(final Properties mqttClientProperties)
      throws GeneralSecurityException {

    final String trustStoreType =
        mqttClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME);
    final String trustStorePath =
        mqttClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME);
    final String trustStorePW =
        mqttClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME);

    KeyStore trustStore;
    if (Files.exists(Paths.get(trustStorePath))) {
      LOG.info("Load external truststore from path: {}", trustStorePath);
      trustStore = loadTrustStoreFromExternalPath(trustStoreType, trustStorePath, trustStorePW);
    } else {
      LOG.info("Load default truststore from classpath: {}", trustStorePath);
      trustStore = loadTrustStoreFromClassPath(trustStoreType, trustStorePath, trustStorePW);
    }

    final TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    return tmf;
  }

  /** Implementations must override this to define behavior after connecting */
  abstract void onConnect(Mqtt3BlockingClient client);

  public boolean isRunning() {
    return this.running;
  }

  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::stopClient));
  }

  private void stopClient() {
    LOG.info("Stopping client {}", this.getClass().getSimpleName());
    this.disconnect();
    this.running = false;
    LOG.info("Client {} stopped", this.getClass().getSimpleName());
  }

  private void disconnect() {
    try {
      Thread.sleep(2000);
    } catch (final InterruptedException e) {
      LOG.error("Interrupted during sleep", e);
    }
    if (this.mqtt3BlockingClient != null) {
      this.mqtt3BlockingClient.disconnect();
    }
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
