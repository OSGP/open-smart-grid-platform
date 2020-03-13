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
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LnaClient extends Client {

    private static final Logger LOG = LoggerFactory.getLogger(LnaClient.class);

    private final SimulatorSpec simulatorSpec;
    private int i = 0;

    public LnaClient(final SimulatorSpec simulatorSpec) {
        super(simulatorSpec.getBrokerHost(), simulatorSpec.getBrokerPort());
        this.simulatorSpec = simulatorSpec;
    }

    @Override
    void onConnect(final Mqtt3BlockingClient client) {
        if (this.hasMessages()) {
            while (this.isRunning()) {
                final Message message = this.getNextMessage();
                this.publish(client, message);
                this.pause(message.getPauseMillis());
            }
        }
    }

    private Message getNextMessage() {
        final Message[] messages = this.simulatorSpec.getMessages();
        if (this.i >= messages.length) {
            this.i = 0;
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
        }
    }

    public void publish(final Mqtt3BlockingClient client, final Message message) {
        client.publishWith().topic(message.getTopic()).qos(MqttQos.AT_LEAST_ONCE).payload(
                message.getPayload().getBytes()).send();
    }
}
