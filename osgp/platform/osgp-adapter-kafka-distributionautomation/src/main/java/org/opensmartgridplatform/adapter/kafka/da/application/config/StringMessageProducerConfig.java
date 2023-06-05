// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
