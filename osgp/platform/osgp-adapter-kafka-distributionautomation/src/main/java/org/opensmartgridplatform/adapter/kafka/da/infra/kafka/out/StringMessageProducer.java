/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StringMessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageProducer.class);

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public StringMessageProducer(
      @Qualifier("distributionAutomationMessageKafkaTemplate")
          final KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(final String message) {
    LOGGER.info("StringMessageProducer.send is called with message: {}", message);
    /*
     * No need for callback functionality now; by default, the template is configured with a
     * LoggingProducerListener, which logs errors and does nothing when the send is successful.
     */
    this.kafkaTemplate.sendDefault(message);
  }
}
