//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.opensmartgridplatform.simulator.protocol.mqtt.zip.PayloadZipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatorSpecPublishingClient extends Client {

  private static final Logger LOG = LoggerFactory.getLogger(SimulatorSpecPublishingClient.class);

  private final SimulatorSpec simulatorSpec;
  private int i = 0;

  public SimulatorSpecPublishingClient(
      final SimulatorSpec simulatorSpec,
      final boolean cleanSession,
      final int keepAlive,
      final MqttClientSslConfig mqttClientSslConfig) {
    super(
        simulatorSpec.getBrokerHost(),
        simulatorSpec.getBrokerPort(),
        cleanSession,
        keepAlive,
        mqttClientSslConfig);
    this.simulatorSpec = simulatorSpec;
  }

  @Override
  void onConnect(final Mqtt3BlockingClient client) {
    if (this.hasMessages()) {
      while (this.isRunning()) {
        final Message message = this.getNextMessage();
        if (message == null) {
          this.stopClient();
          return;
        }
        this.publish(client, message);
        this.pause(message.getPauseMillis());
      }
    }
  }

  private Message getNextMessage() {
    final Message[] messages = this.simulatorSpec.getMessages();
    if (this.i >= messages.length) {
      if (this.simulatorSpec.keepReplayingMessages()) {
        this.i = 0;
      } else {
        return null;
      }
    }
    return messages[this.i++];
  }

  private boolean hasMessages() {
    return this.simulatorSpec.getMessages() != null && this.simulatorSpec.getMessages().length > 0;
  }

  private void pause(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (final InterruptedException e) {
      LOG.warn("Interrupted sleep", e);
      Thread.currentThread().interrupt();
    }
  }

  public void publish(final Mqtt3BlockingClient client, final Message message) {

    LOG.info(
        "{} identified by {} is about to publish a {} message on topic {}",
        SimulatorSpecPublishingClient.class.getSimpleName(),
        this.uuid,
        message.getZip() ? "zipped" : "text",
        message.getTopic());

    final byte[] payload;
    if (message.getZip()) {
      payload = PayloadZipper.gzip(message.getPayload());
    } else {
      payload = message.getPayload();
    }
    client
        .publishWith()
        .topic(message.getTopic())
        .qos(MqttQos.AT_LEAST_ONCE)
        .payload(payload)
        .send();
  }
}
