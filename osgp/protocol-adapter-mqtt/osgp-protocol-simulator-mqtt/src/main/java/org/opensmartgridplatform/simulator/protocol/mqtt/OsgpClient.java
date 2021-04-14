/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgpClient extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(OsgpClient.class);
  private static final String DEFAULT_TOPIC = "+/measurement";

  private final String topic;
  private final UUID uuid;
  private final String host;
  private final int port;

  private Mqtt3AsyncClient client;

  public OsgpClient(final String host, final int port, final String topic) {
    this.host = host;
    this.port = port;
    this.uuid = UUID.randomUUID();
    this.topic = topic;
  }

  @Override
  public void run() {
    this.client =
        Mqtt3Client.builder()
            .identifier(this.uuid.toString())
            .serverHost(this.host)
            .serverPort(this.port)
            .buildAsync();
    this.client
        .connectWith()
        .send()
        .whenComplete(
            (ack, throwable) -> {
              if (throwable != null) {
                LOG.info(
                    String.format(
                        "Client %s startup failed: %s",
                        this.getClass().getSimpleName(), throwable.getMessage()));
              } else {
                LOG.info(
                    String.format(
                        "Client %s received Ack %s",
                        this.getClass().getSimpleName(), ack.getType()));
                LOG.info(String.format("Client %s started", this.getClass().getSimpleName()));
                this.onConnect(this.client);
              }
            });
  }

  void onConnect(final Mqtt3AsyncClient client) {
    client
        .subscribeWith()
        .topicFilter(this.topic)
        .qos(MqttQos.AT_LEAST_ONCE)
        .callback(this::receive)
        .send()
        .whenComplete(
            (subAck, throwable) -> {
              if (throwable != null) {
                LOG.info(
                    String.format(
                        "Client %s subscription failed: %s",
                        this.getClass().getSimpleName(), throwable.getMessage()));
              } else {
                LOG.info(String.format("Client %s subscribed", this.getClass().getSimpleName()));
              }
            });
  }

  private void receive(final Mqtt3Publish publish) {
    publish
        .getPayload()
        .ifPresent(
            p ->
                LOG.info(
                    String.format("%s payload:%s%n", p, new String(publish.getPayloadAsBytes()))));
  }

  public static void main(final String[] args) {
    final int l = args.length;
    final String host = l >= 1 ? args[0] : Default.BROKER_HOST;
    final int port = l >= 2 ? Integer.parseInt(args[1]) : Default.BROKER_PORT;
    final String topic = l >= 3 ? args[2] : DEFAULT_TOPIC;
    new OsgpClient(host, port, topic).start();
  }
}
