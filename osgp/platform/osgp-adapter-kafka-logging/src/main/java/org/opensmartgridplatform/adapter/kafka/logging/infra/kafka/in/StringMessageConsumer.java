/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.opensmartgridplatform.adapter.kafka.logging.config.StringMessageLoggingEnabled;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Conditional(StringMessageLoggingEnabled.class)
public class StringMessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageConsumer.class);

  @Autowired private KafkaLogger kafkaLogger;

  @KafkaListener(
      containerFactory = "distributionAutomationMessageKafkaListenerContainerFactory",
      topics = "${distributionautomation.kafka.topic.message}")
  public void listen(final ConsumerRecord<String, String> consumerRecord) {
    this.kafkaLogger.log(consumerRecord);

    final String topic = consumerRecord.topic();
    final Object key = consumerRecord.key();
    LOGGER.debug("Consumer received message on topic \"{}\" with key \"{}\"", topic, key);
  }
}
