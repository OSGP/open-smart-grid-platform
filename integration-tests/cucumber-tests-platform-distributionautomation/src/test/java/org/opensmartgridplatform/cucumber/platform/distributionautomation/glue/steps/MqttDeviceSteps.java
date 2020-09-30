/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.util.Utf8;
import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.AnalogValue;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.PeakShavingConsumer;
import org.opensmartgridplatform.simulator.protocol.mqtt.SimulatorSpecPublishingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MqttDeviceSteps {

    @Autowired
    private MqttDeviceRepository mqttDeviceRepository;

    @Autowired
    private PeakShavingConsumer consumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttDeviceSteps.class);

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
        LOGGER.info("Payload: {}", payload);
        final Message message = new Message(topic, payload, 10000);
        final Message[] messages = { message };
        spec.setMessages(messages);
        final SimulatorSpecPublishingClient publishingClient = new SimulatorSpecPublishingClient(spec);
        publishingClient.start();
    }

    @Then("a message is published to Kafka")
    public void aMessageIsPublishedToKafka(final Map<String, String> parameters) {
        final String description = getString(parameters, PlatformDistributionAutomationKeys.DESCRIPTION);
        final String kind = getString(parameters, PlatformDistributionAutomationKeys.KIND);
        final PowerSystemResource powerSystemResource = new PowerSystemResource(new Utf8(description), null,
                new ArrayList<Name>());

        final List<Analog> measurements = new ArrayList<>();
        for (int index = 1; index <= getInteger(parameters,
                PlatformDistributionAutomationKeys.NUMBER_OF_ELEMENTS); index++) {
            final String elementStart = "measurement" + index + "_";
            final String elementDescription = getString(parameters,
                    elementStart + PlatformDistributionAutomationKeys.DESCRIPTION);
            final UnitSymbol unitSymbol = getEnum(parameters,
                    elementStart + PlatformDistributionAutomationKeys.UNIT_SYMBOL, UnitSymbol.class);
            final UnitMultiplier unitMultiplier = getEnum(parameters,
                    elementStart + PlatformDistributionAutomationKeys.UNIT_MULTIPLIER, UnitMultiplier.class,
                    PlatformDistributionAutomationDefaults.UNIT_MULTIPLIER);
            final Float value = getFloat(parameters, elementStart + PlatformDistributionAutomationKeys.VALUE);
            measurements.add(this.createAnalog(elementDescription, value, unitSymbol, unitMultiplier));
        }
        final GridMeasurementPublishedEvent expectedMessage = new GridMeasurementPublishedEvent(
                System.currentTimeMillis(), new Utf8(description), null, new Utf8(kind), new ArrayList<Name>(),
                powerSystemResource, measurements);

        this.consumer.checkKafkaOutput(expectedMessage);
    }

    private Analog createAnalog(final String description, final Float value, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        return new Analog(new Utf8(description), UUID.randomUUID().toString(), AccumulationKind.none,
                MeasuringPeriodKind.none, PhaseCode.none, unitMultiplier, unitSymbol, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(value, null, null)));
    }

}
