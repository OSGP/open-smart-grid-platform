/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import com.alliander.data.scadameasurementpublishedevent.AccumulationKind;
import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.AnalogValue;
import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.MeasuringPeriodKind;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.NameType;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;
import com.alliander.data.scadameasurementpublishedevent.Voltage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.avro.util.Utf8;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.LowVoltageMessageConsumer;
import org.opensmartgridplatform.simulator.protocol.mqtt.SimulatorSpecPublishingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MqttDeviceSteps {

  @Autowired private MqttDeviceRepository mqttDeviceRepository;

  @Autowired private LowVoltageMessageConsumer consumer;

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttDeviceSteps.class);

  private static final float LOW_VOLTAGE_NOMINAL = 0.4f;

  @When("MQTT device {string} sends a measurement report")
  public void theDeviceSendsAMeasurementReport(
      final String deviceIdentification, final Map<String, String> parameters) throws IOException {

    final MqttDevice device =
        this.mqttDeviceRepository.findByDeviceIdentification(deviceIdentification);
    final String host = device.getHost();
    final int port = device.getPort();
    final String topic = device.getTopics();

    final SimulatorSpec spec = new SimulatorSpec(host, port);
    spec.setStartupPauseMillis(2000);
    final String payload = parameters.get(PlatformDistributionAutomationKeys.PAYLOAD);
    LOGGER.info("Payload: {}", payload);
    final Message message = new Message(topic, payload, 10000);
    final Message[] messages = {message};
    spec.setMessages(messages);
    final SimulatorSpecPublishingClient publishingClient = new SimulatorSpecPublishingClient(spec);
    publishingClient.start();
  }

  @Then("a message is published to Kafka")
  public void aMessageIsPublishedToKafka(final Map<String, String> parameters) {

    final Voltage voltage = new Voltage(UnitMultiplier.k, UnitSymbol.V, LOW_VOLTAGE_NOMINAL);

    final ConductingEquipment powerSystemResource =
        new ConductingEquipment(
            new BaseVoltage(new Utf8("LS"), voltage), this.getNames(parameters));

    final List<Analog> measurements = new ArrayList<>();
    for (int index = 1;
        index <= getInteger(parameters, PlatformDistributionAutomationKeys.NUMBER_OF_ELEMENTS);
        index++) {
      final String elementStart = "measurement" + index + "_";
      final String elementDescription =
          getString(parameters, elementStart + PlatformDistributionAutomationKeys.DESCRIPTION);
      final UnitSymbol unitSymbol =
          getEnum(
              parameters,
              elementStart + PlatformDistributionAutomationKeys.UNIT_SYMBOL,
              UnitSymbol.class);
      final UnitMultiplier unitMultiplier =
          getEnum(
              parameters,
              elementStart + PlatformDistributionAutomationKeys.UNIT_MULTIPLIER,
              UnitMultiplier.class,
              PlatformDistributionAutomationDefaults.UNIT_MULTIPLIER);
      final Float value =
          getFloat(parameters, elementStart + PlatformDistributionAutomationKeys.VALUE);
      measurements.add(this.createAnalog(elementDescription, value, unitSymbol, unitMultiplier));
    }

    final ScadaMeasurementPublishedEvent expectedMessage =
        new ScadaMeasurementPublishedEvent(
            measurements, powerSystemResource, System.currentTimeMillis(), null, null);

    this.consumer.checkKafkaOutput(expectedMessage);
  }

  private ArrayList<Name> getNames(final Map<String, String> parameters) {
    final String substationIdentification =
        getString(parameters, PlatformDistributionAutomationKeys.SUBSTATION_IDENTIFICATION);
    final String version = getString(parameters, PlatformDistributionAutomationKeys.VERSION);
    final String substationName =
        getString(parameters, PlatformDistributionAutomationKeys.SUBSTATION_NAME);
    final String bayPosition =
        getString(parameters, PlatformDistributionAutomationKeys.BAY_POSITION);
    final String bayIdentification =
        getString(parameters, PlatformDistributionAutomationKeys.BAY_IDENTIFICATION);
    final String assetLabel =
        getString(parameters, PlatformDistributionAutomationKeys.FEEDER_ASSET_LABEL);

    final ArrayList<Name> names = new ArrayList<>();
    names.add(new Name(new NameType("gisbehuizingnummer"), substationIdentification));
    names.add(new Name(new NameType("versie"), version));
    names.add(new Name(new NameType("msr naam"), substationName));
    names.add(new Name(new NameType("bay positie"), bayPosition));
    names.add(new Name(new NameType("bay identificatie"), bayIdentification));
    names.add(new Name(new NameType("functieplaatslabel"), assetLabel));
    return names;
  }

  private Analog createAnalog(
      final String description,
      final Float value,
      final UnitSymbol unitSymbol,
      final UnitMultiplier unitMultiplier) {
    return new Analog(
        Arrays.asList(new AnalogValue(null, value)),
        AccumulationKind.none,
        new Utf8(description),
        MeasuringPeriodKind.none,
        unitMultiplier,
        unitSymbol);
  }
}
