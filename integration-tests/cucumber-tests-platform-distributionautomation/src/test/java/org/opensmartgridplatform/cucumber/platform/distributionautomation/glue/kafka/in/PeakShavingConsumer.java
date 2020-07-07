/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PeakShavingConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeakShavingConsumer.class);

    @Value("${peakshaving.kafka.consumer.wait.fail.duration:90000}")
    private long waitFailMillis;

    private ConsumerRecord<String, String> consumerRecord;

    @KafkaListener(containerFactory = "peakShavingKafkaListenerContainerFactory", topics = "${peakshaving.kafka.topic}")
    public void listen(final ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("recevied consumerRecord");
        this.consumerRecord = consumerRecord;
    }

    public void checkKafkaOutput(final String expectedMessage) {

        final long startTime = System.currentTimeMillis();
        long remaining = this.waitFailMillis;
        while (remaining > 0 || this.consumerRecord != null) {
            final long elapsed = System.currentTimeMillis() - startTime;
            remaining = this.waitFailMillis - elapsed;
        }
        assertThat(this.consumerRecord).isNotNull();
        assertThat(this.consumerRecord.value()).isEqualTo(expectedMessage);
    }

}
