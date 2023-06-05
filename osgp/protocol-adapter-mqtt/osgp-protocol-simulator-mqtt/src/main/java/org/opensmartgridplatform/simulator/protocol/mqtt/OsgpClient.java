// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

  public OsgpClient(final String host, final int port, final String topic) {
    this.host = host;
    this.port = port;
    this.uuid = UUID.randomUUID();
    this.topic = topic;
  }

  @Override
  public void run() {
    final Mqtt3AsyncClient client =
        Mqtt3Client.builder()
            .identifier(this.uuid.toString())
            .serverHost(this.host)
            .serverPort(this.port)
            .buildAsync();
    client
        .connectWith()
        .send()
        .whenComplete(
            (ack, throwable) -> {
              if (throwable != null) {
                LOG.error(
                    "{} identified by {} failed to connect to host on {}:{}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    this.host,
                    this.port,
                    throwable);
              } else {
                LOG.info(
                    "{} identified by {} upon connecting to host on {}:{} received {}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    this.host,
                    this.port,
                    ack);
                LOG.info(
                    "{} identified by {} started and about to subscribe on topic {}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    this.topic);
                this.onConnect(client);
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
                LOG.error(
                    "{} identified by {} failed to subscribe to topic {}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    this.topic,
                    throwable);
              } else {
                LOG.info(
                    "{} identified by {} subscribed to topic {} and received {}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    this.topic,
                    subAck);
              }
            });
  }

  private void receive(final Mqtt3Publish publish) {
    publish
        .getPayload()
        .ifPresent(
            p ->
                LOG.info(
                    "{} identified by {} received published message with payload:{}{}",
                    this.getClass().getSimpleName(),
                    this.uuid,
                    System.lineSeparator(),
                    new String(publish.getPayloadAsBytes())));
  }

  public static void main(final String[] args) {
    final int l = args.length;
    final String host = l >= 1 ? args[0] : Default.BROKER_HOST;
    final int port = l >= 2 ? Integer.parseInt(args[1]) : Default.BROKER_PORT;
    final String topic = l >= 3 ? args[2] : DEFAULT_TOPIC;
    new OsgpClient(host, port, topic).start();
  }
}
