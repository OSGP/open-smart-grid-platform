/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.config;

import com.alliander.data.scadameasurementpublishedevent.Message;
import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
@Conditional(LowVoltageMessageLoggingEnabled.class)
public class LowVoltageMessageConsumerConfig extends AbstractKafkaConsumerConfig<String, Message> {

  public LowVoltageMessageConsumerConfig(
      final Environment environment,
      @Value("${low.voltage.kafka.common.properties.prefix}") final String propertiesPrefix,
      @Value("${low.voltage.kafka.topic}") final String topic,
      @Value("${low.voltage.kafka.consumer.concurrency}") final int concurrency,
      @Value("${low.voltage.kafka.consumer.poll.timeout}") final int pollTimeout) {

    super(environment, propertiesPrefix, topic, concurrency, pollTimeout);
  }

  @Bean("lowVoltageMessageConsumerFactory")
  @Override
  public ConsumerFactory<String, Message> consumerFactory() {
    return this.getConsumerFactory();
  }

  @Bean("lowVoltageMessageKafkaListenerContainerFactory")
  @Override
  public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
    return this.getKafkaListenerContainerFactory();
  }
}
