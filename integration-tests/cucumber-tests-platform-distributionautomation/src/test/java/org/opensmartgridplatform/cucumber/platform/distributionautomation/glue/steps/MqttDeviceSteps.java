/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.PeakShavingConsumer;
import org.opensmartgridplatform.simulator.protocol.mqtt.SimulatorSpecPublishingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MqttDeviceSteps {

    @Autowired
    private MqttDeviceRepository mqttDeviceRepository;

    @Autowired
    private PeakShavingConsumer consumer;

    @When("MQTT device {string} sends a measurement report")
    public void theDeviceSendsAMeasurementReport(final String deviceIdentification,
            final Map<String, String> parameters) throws IOException {

        final MqttDevice device = this.mqttDeviceRepository.findByDeviceIdentification(deviceIdentification);
        final String host = device.getHost();
        final int port = device.getPort();
        final String topic = device.getTopics();

        final SimulatorSpec spec = new SimulatorSpec(host, port);
        spec.setStartupPauseMillis(2000);
        final String payload = parameters.get(PlatformDistributionAutomationKeys.PAYLOAD);
        final Message message = new Message(topic, payload, 10000);
        final Message[] messages = { message };
        spec.setMessages(messages);
        final SimulatorSpecPublishingClient publishingClient = new SimulatorSpecPublishingClient(spec);
        publishingClient.start();
    }

    @Then("a message is published to Kafka")
    public void aMessageIsPublishedToKafka(final Map<String, String> parameters) {
        final String expectedMessage = getString(parameters, PlatformDistributionAutomationKeys.MESSAGE);

        this.consumer.checkKafkaOutput(expectedMessage);
    }

}
