/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.config;

import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.opensmartgridplatform.kafka.logging.Slf4jKafkaLogger;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.adapter.kafka.logging",
      "org.opensmartgridplatform.shared.application.config"
    })
@PropertySource("classpath:osgp-adapter-kafka-logging.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterKafkaLogging/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  @Bean
  public KafkaLogger kafkaLogger() {
    return new Slf4jKafkaLogger();
  }
}
