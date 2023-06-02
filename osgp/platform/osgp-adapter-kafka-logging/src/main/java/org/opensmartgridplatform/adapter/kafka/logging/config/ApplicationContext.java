//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.kafka.logging.config;

import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.opensmartgridplatform.kafka.logging.Slf4jKafkaLogger;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.adapter.kafka.logging",
      "org.opensmartgridplatform.shared.application.config"
    })
@Import({MetricsConfig.class})
@PropertySource("classpath:osgp-adapter-kafka-logging.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterKafkaLogging/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  @Bean
  public KafkaLogger kafkaLogger() {
    return new Slf4jKafkaLogger();
  }
}
