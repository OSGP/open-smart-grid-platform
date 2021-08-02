/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import com.alliander.data.scadameasurementpublishedevent.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MediumVoltageMessageConsumer extends AbstractMessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MediumVoltageMessageConsumer.class);

  public MediumVoltageMessageConsumer(
      @Value("${medium.voltage.kafka.consumer.wait.fail.duration:90000}")
          final long waitFailMillis) {
    super(waitFailMillis);
  }

  @KafkaListener(
      containerFactory = "mediumVoltageMessageKafkaListenerContainerFactory",
      topics = "${medium.voltage.kafka.topic}")
  public void listen(final ConsumerRecord<String, Message> consumerRecord) {
    LOGGER.info("Received consumerRecord");
    this.consumerRecord = consumerRecord;
  }
}
