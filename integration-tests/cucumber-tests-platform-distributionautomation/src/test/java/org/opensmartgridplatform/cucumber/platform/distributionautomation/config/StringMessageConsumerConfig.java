// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
      @Value("${distributionautomation.kafka.topic.message}") final String topic,
      @Value("${distributionautomation.kafka.consumer.concurrency}") final int concurrency,
      @Value("${distributionautomation.kafka.consumer.poll.timeout}") final int pollTimeout) {

    super(environment, propertiesPrefix, topic, concurrency, pollTimeout);
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
