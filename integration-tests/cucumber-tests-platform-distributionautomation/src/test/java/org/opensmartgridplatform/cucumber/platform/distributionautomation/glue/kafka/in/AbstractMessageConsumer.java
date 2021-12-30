/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageConsumer.class);

  private List<ConsumerRecord<String, Message>> consumerRecords;
  private final long waitFailMillis;

  protected AbstractMessageConsumer(final long waitFailMillis) {
    this.waitFailMillis = waitFailMillis;
  }

  public void checkKafkaOutput(final ScadaMeasurementPublishedEvent expectedMessage) {
    final long startTime = System.currentTimeMillis();
    long remaining = this.waitFailMillis;

    final String expectedSubstation = getSubstationFromMessage(expectedMessage);
    while (remaining > 0
        && this.consumerRecords.stream()
            .noneMatch(
                cr ->
                    this.meetsConditions(
                        cr, expectedSubstation, expectedMessage.getPowerSystemResource()))) {
      final long elapsed = System.currentTimeMillis() - startTime;
      remaining = this.waitFailMillis - elapsed;
    }

    final ConsumerRecord<String, Message> consumerRecord =
        this.consumerRecords.stream()
            .filter(
                cr ->
                    this.meetsConditions(
                        cr, expectedSubstation, expectedMessage.getPowerSystemResource()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("ConsumerRecord should be present"));
    final ScadaMeasurementPublishedEvent event = consumerRecord.value().getPayload();
    assertThat(event.getMeasurements())
        .usingElementComparatorIgnoringFields("mRID")
        .isEqualTo(expectedMessage.getMeasurements());
  }

  private boolean meetsConditions(
      final ConsumerRecord<String, Message> consumerRecord,
      final String substation,
      final ConductingEquipment powerSystemResource) {
    return consumerRecord != null
        && this.meetsConditions(consumerRecord.value(), substation, powerSystemResource);
  }

  private boolean meetsConditions(
      final Message msg, final String substation, final ConductingEquipment powerSystemResource) {
    return msg.getMessageId() != null
        && msg.getProducerId().equals("GXF")
        && this.meetsConditions(msg.getPayload(), substation, powerSystemResource);
  }

  private boolean meetsConditions(
      final ScadaMeasurementPublishedEvent event,
      final String substation,
      final ConductingEquipment powerSystemResource) {
    return event.getPowerSystemResource().equals(powerSystemResource)
        && getSubstationFromMessage(event).equals(substation);
  }

  private static boolean waitForConsumerRecordFromSubstation(
      final String substationIdentification, final ConsumerRecord<String, Message> consumerRecord) {
    return consumerRecord == null
        || !isMessageFromExpectedSubstation(substationIdentification, consumerRecord.value());
  }

  private static boolean isMessageFromExpectedSubstation(
      final String expectedSubstation, final Message message) {
    final String actualSubstation = getSubstationFromMessage(message.getPayload());
    LOGGER.info(
        "Checking if message (actual substation: {}) is from expected substation: {}.",
        actualSubstation,
        expectedSubstation);
    return expectedSubstation.equalsIgnoreCase(actualSubstation);
  }

  private static String getSubstationFromMessage(final ScadaMeasurementPublishedEvent event) {
    if (event.getPowerSystemResource() == null
        || event.getPowerSystemResource().getNames() == null) {
      return StringUtils.EMPTY;
    }
    return event.getPowerSystemResource().getNames().stream()
        .filter(AbstractMessageConsumer::isSubstationName)
        .map(n -> n.getName().toString())
        .findFirst()
        .orElse(StringUtils.EMPTY);
  }

  private static boolean isSubstationName(final Name n) {
    return n != null
        && n.getNameType().getDescription() != null
        && "gisbehuizingnummer".equals(n.getNameType().getDescription().toString());
  }

  protected void addConsumerRecord(final ConsumerRecord<String, Message> consumerRecord) {
    this.consumerRecords.add(consumerRecord);
  }
}
