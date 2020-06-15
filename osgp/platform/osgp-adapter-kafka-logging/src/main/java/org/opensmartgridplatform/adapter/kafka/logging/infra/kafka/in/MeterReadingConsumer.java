/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.adapter.kafka.logging.config.DistributionAutomationLoggingEnabled;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Conditional(DistributionAutomationLoggingEnabled.class)
public class MeterReadingConsumer {

    @Autowired
    private KafkaLogger kafkaLogger;

    @KafkaListener(containerFactory = "distributionAutomationKafkaListenerContainerFactory",
            topics = "${distributionautomation.kafka.topic}")
    public void listen(final ConsumerRecord<String, MeterReading> consumerRecord) {
        this.kafkaLogger.log(consumerRecord);
    }

}
