/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.domain.commands;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCommandExecutor.class);

    public SubscriptionCommandExecutor() {
        //
    }

    public void execute(final String ipAddress, final int port, final String topic) {

        final String identifier = String.valueOf(this.hashCode());
        final Mqtt3AsyncClient client = Mqtt3Client.builder().identifier(identifier).serverHost(ipAddress).serverPort(
                port).buildAsync();
        client.connectWith().send().whenComplete((ack, throwable) -> {
            if (throwable != null) {
                LOG.info(String.format("Client %s startup failed: %s", this.getClass().getSimpleName(),
                        throwable.getMessage()));
            } else {
                LOG.info(String.format("Client %s received Ack %s", this.getClass().getSimpleName(), ack.getType()));
                LOG.info(String.format("Client %s started", this.getClass().getSimpleName()));
                this.onConnect(client, topic);
            }
        });
    }

    void onConnect(final Mqtt3AsyncClient client, final String topic) {
        client.subscribeWith().topicFilter(topic).qos(MqttQos.AT_LEAST_ONCE).callback(
                this::receive).send().whenComplete((subAck, throwable) -> {
            if (throwable != null) {
                LOG.info(String.format("Client %s subscription failed: %s", this.getClass().getSimpleName(),
                        throwable.getMessage()));
            } else {
                LOG.info(String.format("Client %s subscribed", this.getClass().getSimpleName()));
            }
        });
    }

    private void receive(final Mqtt3Publish publish) {
        publish.getPayload().ifPresent(p -> {
            LOG.info(String.format("%s payload:%s%n", p, new String(publish.getPayloadAsBytes())));
        });
    }
}
