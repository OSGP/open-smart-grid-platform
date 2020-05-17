/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PeakShavingProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeakShavingProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public PeakShavingProducer(@Qualifier("peakShavingKafkaTemplate") final KafkaTemplate<String, String> kafkaTemplate,
            final DistributionAutomationMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final String message) {

        try {
            this.kafkaTemplate.sendDefault(message);
        } catch (final RuntimeException e) {
            LOGGER.error("Sending message {} failed", message, e);
        }
    }

}
