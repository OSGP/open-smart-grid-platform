/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.config;

import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
@EnableKafka
public class StringMessageConsumerConfig extends AbstractKafkaConsumerConfig<String, String> {

  public StringMessageConsumerConfig(
      final Environment environment,
      @Value("${distributionautomation.kafka.common.properties.prefix}")
          final String propertiesPrefix,
      @Value("${distributionautomation.kafka.consumer.concurrency}") final int concurrency,
      @Value("${distributionautomation.kafka.consumer.poll.timeout}") final int pollTimeout) {

    super(environment, propertiesPrefix, concurrency, pollTimeout);
  }

  @Bean("distributionAutomationMessageConsumerFactory")
  @Override
  public ConsumerFactory<String, String> consumerFactory() {
    return this.getConsumerFactory();
  }

  @Bean("distributionAutomationMessageKafkaListenerContainerFactory")
  @Override
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    return this.getKafkaListenerContainerFactory();
  }
}
