/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig extends AbstractKafkaProducerConfig<String, MeterReading> {

    @Bean("distributionAutomationKafkaTemplate")
    public KafkaTemplate<String, MeterReading> kafkaTemplate(
            @Value("${distributionautomation.kafka.common.properties.prefix}") final String propertiesPrefix,
            @Value("${distributionautomation.kafka.producer.topic}") final String topic) {
        return this.initKafkaTemplate(propertiesPrefix, topic);
    }
}
