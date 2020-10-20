/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.config;

import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

@Configuration
@Conditional(ScadaMeasurementPublishedEventLoggingEnabled.class)
public class ScadaMeasurementPublishedEventKafkaConsumerConfig
        extends AbstractKafkaConsumerConfig<String, ScadaMeasurementPublishedEvent> {

    public ScadaMeasurementPublishedEventKafkaConsumerConfig(final Environment environment,
            @Value("${scada.measurement.published.event.kafka.common.properties.prefix}") final String propertiesPrefix,
            @Value("${scada.measurement.published.event.kafka.topic}") final String topic,
            @Value("${scada.measurement.published.event.kafka.consumer.concurrency}") final int concurrency,
            @Value("${scada.measurement.published.event.kafka.consumer.poll.timeout}") final int pollTimeout) {

        super(environment, propertiesPrefix, topic, concurrency, pollTimeout);
    }

    @Bean("scadaMeasurementPublishedEventConsumerFactory")
    @Override
    public ConsumerFactory<String, ScadaMeasurementPublishedEvent> consumerFactory() {
        return this.getConsumerFactory();
    }

    @Bean("scadaMeasurementPublishedEventKafkaListenerContainerFactory")
    @Override
    public ConcurrentKafkaListenerContainerFactory<String, ScadaMeasurementPublishedEvent> kafkaListenerContainerFactory() {
        return this.getKafkaListenerContainerFactory();
    }

}
