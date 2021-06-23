/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LowVoltageMessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LowVoltageMessageConsumer.class);

  @Value("${low.voltage.kafka.consumer.wait.fail.duration:90000}")
  private long waitFailMillis;

  private ConsumerRecord<String, Message> consumerRecord;

  @KafkaListener(
      containerFactory = "lowVoltageMessageKafkaListenerContainerFactory",
      topics = "${low.voltage.kafka.topic}")
  public void listen(final ConsumerRecord<String, Message> consumerRecord) {
    LOGGER.info("received consumerRecord");
    this.consumerRecord = consumerRecord;
  }

  public void checkKafkaOutput(final ScadaMeasurementPublishedEvent expectedMessage) {

    final long startTime = System.currentTimeMillis();
    long remaining = this.waitFailMillis;

    final String expectedSubstation = getSubstationFromMessage(expectedMessage);
    while (remaining > 0
        && waitForConsumerRecordFromSubstation(expectedSubstation, this.consumerRecord)) {
      final long elapsed = System.currentTimeMillis() - startTime;
      remaining = this.waitFailMillis - elapsed;
    }
    assertThat(this.consumerRecord).isNotNull();
    final Message message = this.consumerRecord.value();
    assertThat(message.getMessageId()).isNotNull();
    assertThat(message.getProducerId()).hasToString("GXF");
    final ScadaMeasurementPublishedEvent event = message.getPayload();
    assertThat(event.getPowerSystemResource()).isEqualTo(expectedMessage.getPowerSystemResource());
    assertThat(event.getMeasurements())
        .usingElementComparatorIgnoringFields("mRID")
        .isEqualTo(expectedMessage.getMeasurements());
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
        .filter(LowVoltageMessageConsumer::isSubstationName)
        .map(n -> n.getName().toString())
        .findFirst()
        .orElse(StringUtils.EMPTY);
  }

  private static boolean isSubstationName(final Name n) {
    return n != null
        && n.getNameType().getDescription() != null
        && "gisbehuizingnummer".equals(n.getNameType().getDescription().toString());
  }
}
