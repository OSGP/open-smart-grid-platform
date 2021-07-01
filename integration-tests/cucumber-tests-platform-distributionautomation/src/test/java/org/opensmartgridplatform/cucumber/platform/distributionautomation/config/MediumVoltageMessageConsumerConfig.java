/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.config;

import com.alliander.data.scadameasurementpublishedevent.Message;
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
public class MediumVoltageMessageConsumerConfig
    extends AbstractKafkaConsumerConfig<String, Message> {

  public MediumVoltageMessageConsumerConfig(
      final Environment environment,
      @Value("${medium.voltage.kafka.common.properties.prefix}") final String propertiesPrefix,
      @Value("${medium.voltage.kafka.topic}") final String topic,
      @Value("${medium.voltage.kafka.consumer.concurrency}") final int concurrency,
      @Value("${medium.voltage.kafka.consumer.poll.timeout}") final int pollTimeout) {

    super(environment, propertiesPrefix, topic, concurrency, pollTimeout);
  }

  @Bean("mediumVoltageMessageConsumerFactory")
  @Override
  public ConsumerFactory<String, Message> consumerFactory() {
    return this.getConsumerFactory();
  }

  @Bean("mediumVoltageMessageKafkaListenerContainerFactory")
  @Override
  public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
    return this.getKafkaListenerContainerFactory();
  }
}
