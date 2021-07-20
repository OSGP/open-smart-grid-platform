/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.avro.util.Utf8;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.LowVoltageMessageConsumer;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.MediumVoltageMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaPublishingSteps {

  @Autowired private LowVoltageMessageConsumer lowVoltageMessageConsumer;
  @Autowired private MediumVoltageMessageConsumer mediumVoltageMessageConsumer;

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublishingSteps.class);

  private static final float LOW_VOLTAGE_NOMINAL = 0.4f;

  @ParameterType("LOW_VOLTAGE|MEDIUM_VOLTAGE")
  public KafkaMessageType kafkaMessageType(final String deviceType) {
    return KafkaMessageType.valueOf(deviceType);
  }

  public enum KafkaMessageType {
    LOW_VOLTAGE,
    MEDIUM_VOLTAGE
  }

  @Then("a {kafkaMessageType} message is published to Kafka")
  public void aMessageIsPublishedToKafka(
      final KafkaMessageType kafkaMessageType, final Map<String, String> parameters) {

    LOGGER.debug("Then a {} message is published to Kafka.", kafkaMessageType);

    final ScadaMeasurementPublishedEvent expectedMessage = createExpectedMessage(parameters);

    switch (kafkaMessageType) {
      case LOW_VOLTAGE:
        this.lowVoltageMessageConsumer.checkKafkaOutput(expectedMessage);
        break;
      case MEDIUM_VOLTAGE:
        this.mediumVoltageMessageConsumer.checkKafkaOutput(expectedMessage);
        break;
    }
  }

  private static ScadaMeasurementPublishedEvent createExpectedMessage(
      final Map<String, String> parameters) {
    final Voltage voltage = new Voltage(UnitMultiplier.k, UnitSymbol.V, LOW_VOLTAGE_NOMINAL);

    final ConductingEquipment powerSystemResource =
        new ConductingEquipment(new BaseVoltage(new Utf8("LS"), voltage), getNames(parameters));

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
      measurements.add(createAnalog(elementDescription, value, unitSymbol, unitMultiplier));
    }

    return new ScadaMeasurementPublishedEvent(
        measurements, powerSystemResource, System.currentTimeMillis(), null, null);
  }

  private static ArrayList<Name> getNames(final Map<String, String> parameters) {
    final String substationIdentification =
        getString(parameters, PlatformDistributionAutomationKeys.SUBSTATION_IDENTIFICATION);
    final String version = getString(parameters, PlatformDistributionAutomationKeys.VERSION);
    final String substationName =
        getString(parameters, PlatformDistributionAutomationKeys.SUBSTATION_NAME);
    final String fieldCode =
        getString(parameters, PlatformDistributionAutomationKeys.FEEDER_FIELD_CODE);
    final String bayIdentification =
        getString(parameters, PlatformDistributionAutomationKeys.BAY_IDENTIFICATION);
    final String assetLabel =
        getString(parameters, PlatformDistributionAutomationKeys.FEEDER_ASSET_LABEL);

    final ArrayList<Name> names = new ArrayList<>();
    names.add(new Name(new NameType("gisbehuizingnummer"), substationIdentification));
    names.add(new Name(new NameType("versie"), version));
    names.add(new Name(new NameType("msr naam"), substationName));
    if (fieldCode != null) {
      names.add(new Name(new NameType("veld code"), fieldCode));
    }
    names.add(new Name(new NameType("bay identificatie"), bayIdentification));
    if (assetLabel != null) {
      names.add(new Name(new NameType("functieplaatslabel"), assetLabel));
    }
    return names;
  }

  private static Analog createAnalog(
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
