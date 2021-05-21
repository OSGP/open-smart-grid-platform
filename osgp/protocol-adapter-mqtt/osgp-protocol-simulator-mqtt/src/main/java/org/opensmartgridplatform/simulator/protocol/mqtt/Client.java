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
import java.io.InputStream;
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
  private final Properties clientProperties;
  private volatile boolean running;
  private Mqtt3BlockingClient mqtt3BlockingClient;

  protected Client(final String host, final int port, final Properties clientProperties) {
    this.host = host;
    this.port = port;
    this.clientProperties = clientProperties;
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
              .sslConfig(getSslConfig(this.clientProperties))
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

  private static MqttClientSslConfig getSslConfig(final Properties clientProperties)
      throws Exception {
    if (clientProperties == null || clientProperties.isEmpty()) {
      return null;
    }
    return MqttClientSslConfig.builder()
        .trustManagerFactory(getTruststoreFactory(clientProperties))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(final Properties sslClientProperties)
      throws Exception {

    final String trustStoreType =
        sslClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME);
    final String trustStorePath =
        sslClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME);
    final String trustStorePW =
        sslClientProperties.getProperty(ClientConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME);

    final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
    InputStream in = ClassLoader.getSystemResourceAsStream(trustStorePath);
    if (in == null) {
      in = new FileInputStream(trustStorePath);
    }
    trustStore.load(in, trustStorePW.toCharArray());

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
}
