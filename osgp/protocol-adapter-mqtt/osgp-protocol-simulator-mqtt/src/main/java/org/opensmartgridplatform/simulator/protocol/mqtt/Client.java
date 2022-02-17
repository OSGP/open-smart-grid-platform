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
import java.util.UUID;
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
  private final MqttClientSslConfig mqttClientSslConfig;
  private volatile boolean running;
  private Mqtt3BlockingClient mqtt3BlockingClient;

  protected Client(
      final String host, final int port, final MqttClientSslConfig mqttClientSslConfig) {
    this.host = host;
    this.port = port;
    this.mqttClientSslConfig = mqttClientSslConfig;
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
              .sslConfig(this.mqttClientSslConfig)
              .automaticReconnectWithDefaultConfig()
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
      if (this.mqtt3BlockingClient != null) {
        this.mqtt3BlockingClient.disconnect();
      }
    } catch (final InterruptedException e) {
      LOG.error("Interrupted during sleep", e);
      Thread.currentThread().interrupt();
    }
  }
}
