/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.messaging.MessageId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.application.services.LocationService;
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Feeder;
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Location;
import org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in.ScadaMeasurementPayload;
import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.opensmartgridplatform.shared.utils.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Optionals;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VoltageMessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(VoltageMessageProducer.class);

  private static final int META_MEASUREMENT_FEEDER = 100;

  private static final int MV_MEASUREMENT_FEEDER = 200;

  private final DistributionAutomationMapper mapper;

  private final KafkaTemplate<String, Message> lowVoltageKafkaTemplate;

  private final KafkaTemplate<String, Message> mediumVoltageKafkaTemplate;

  private final MessageSigner messageSigner;

  private final LocationService locationService;

  @Autowired
  public VoltageMessageProducer(
      @Qualifier("distributionAutomationLowVoltageKafkaTemplate")
          final KafkaTemplate<String, Message> distributionAutomationLowVoltageKafkaTemplate,
      @Qualifier("distributionAutomationMediumVoltageKafkaTemplate")
          final KafkaTemplate<String, Message> distributionAutomationMediumVoltageKafkaTemplate,
      final MessageSigner messageSigner,
      final DistributionAutomationMapper mapper,
      final LocationService locationService) {

    this.lowVoltageKafkaTemplate = distributionAutomationLowVoltageKafkaTemplate;
    this.mediumVoltageKafkaTemplate = distributionAutomationMediumVoltageKafkaTemplate;
    this.mapper = mapper;
    this.messageSigner = messageSigner;
    this.locationService = locationService;
  }

  public void send(String measurement) {

    LOGGER.info("VoltageMessageProducer.send is called with measurement {}", measurement);

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      // TODO remove this line when the double quotes bug around the measurement field is fixed on
      // the device side to avoid bugs in the future.
      measurement = measurement.replaceAll("(\"\\[)", "[").replaceAll("(\\]\")", "]");
      // we expect a list with one payload from the rtu.
      final ScadaMeasurementPayload[] payloads =
          objectMapper.readValue(measurement, ScadaMeasurementPayload[].class);
      if (payloads.length == 0 || payloads[0] == null) {
        LOGGER.error("Source does not include the correct data fields. Source {}", measurement);
        return;
      } else if (payloads.length > 1) {
        LOGGER.info(
            "Source has more than one payload, we are only processing the first and ignoring the others");
      }

      final ScadaMeasurementPayload payload = this.addLocationData(payloads);
      final int feeder = Integer.parseInt(payload.getFeeder());

      final ScadaMeasurementPublishedEvent event =
          this.mapper.map(payload, ScadaMeasurementPublishedEvent.class);

      LOGGER.debug("Trying to send ScadaMeasurementPublishedEventProducer {}", event);

      if (event != null) {
        final MessageId messageId = new MessageId(UuidUtil.getBytesFromRandomUuid());
        final Message message =
            new Message(messageId, System.currentTimeMillis(), "GXF", null, event);

        this.messageSigner.sign(message);

        /*
         * No need for callback functionality now; by default, the
         * template is configured with a LoggingProducerListener, which
         * logs errors and does nothing when the send is successful.
         */
        if (MV_MEASUREMENT_FEEDER == feeder) {
          LOGGER.debug("Sending medium voltage message: {}", message);
          this.mediumVoltageKafkaTemplate.sendDefault(message);
        } else {
          LOGGER.debug("Sending low voltage message: {}", message);
          this.lowVoltageKafkaTemplate.sendDefault(message);
        }
      }
    } catch (final JsonProcessingException e) {
      LOGGER.error("Error while converting measurement to Json", e);
    }
  }

  private ScadaMeasurementPayload addLocationData(final ScadaMeasurementPayload[] payloads) {
    final ScadaMeasurementPayload payload = payloads[0];
    final String substationIdentification = payload.getSubstationIdentification();

    Optionals.ifPresentOrElse(
        this.locationService.getLocation(substationIdentification),
        location -> addLocationData(payload, location),
        () -> configureWithoutLocation(payload));

    return payload;
  }

  private static void configureWithoutLocation(final ScadaMeasurementPayload payload) {
    payload.setSubstationName("");
    configureWithoutFeeder(payload);
  }

  private static void configureWithoutFeeder(final ScadaMeasurementPayload payload) {
    payload.setBayIdentification("");
    payload.setFieldCode(null);
    payload.setAssetLabel(null);
  }

  private static void addLocationData(
      final ScadaMeasurementPayload payload, final Location location) {
    payload.setSubstationName(location.getName());

    try {
      final int feederNumber = Integer.parseInt(payload.getFeeder());
      if (feederNumber != META_MEASUREMENT_FEEDER) {
        Optionals.ifPresentOrElse(
            location.getFeeder(feederNumber),
            feeder -> addFeederData(payload, feeder),
            () -> configureWithoutFeeder(payload));
      }
    } catch (final NumberFormatException e) {
      LOGGER.error("Payload contains a non-numeric value for feeder", e);
    }
  }

  private static void addFeederData(final ScadaMeasurementPayload payload, final Feeder feeder) {
    payload.setFieldCode(feeder.getFieldCode());
    payload.setBayIdentification(feeder.getName());
    payload.setAssetLabel(feeder.getAssetLabel());
  }
}
