/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import java.util.concurrent.TimeUnit;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgpClient extends Client {

    private static final Logger LOG = LoggerFactory.getLogger(OsgpClient.class);
    private final String topic;

    public OsgpClient(final String host, final int port, final String topic) {
        super(host, port);
        this.topic = topic;
    }

    @Override
    void onConnect(final Mqtt3BlockingClient client) {
        this.subscribe(client);
        try (final Mqtt3BlockingClient.Mqtt3Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            this.receive(publishes);
        }
    }

    private void subscribe(final Mqtt3BlockingClient client) {
        client.subscribeWith().topicFilter(this.topic).qos(MqttQos.AT_LEAST_ONCE).send();
    }

    private void receive(final Mqtt3BlockingClient.Mqtt3Publishes publishes) {
        while (this.isRunning()) {
            try {
                publishes.receive(1, TimeUnit.SECONDS).ifPresent(p -> {
                    LOG.info(String.format("%s payload:%s%n", p, new String(p.getPayloadAsBytes())));
                });
            } catch (final InterruptedException e) {
                LOG.warn("Receive interruped", e);
            }
        }
    }

    public static void main(final String[] args) {
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String topic = args[2];
        new OsgpClient(host, port, topic).start();
    }

}
