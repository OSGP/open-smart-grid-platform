/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class StringMessageProducerConfig extends AbstractKafkaProducerConfig<String, String> {

  public StringMessageProducerConfig(
      final Environment environment,
      @Value("${distributionautomation.kafka.common.properties.prefix}")
          final String propertiesPrefix,
      @Value("${distributionautomation.kafka.topic.message}") final String topic) {
    super(environment, propertiesPrefix, topic);
  }

  @Bean("distributionAutomationMessageKafkaTemplate")
  @Override
  public KafkaTemplate<String, String> kafkaTemplate() {
    return this.getKafkaTemplate();
  }
}
