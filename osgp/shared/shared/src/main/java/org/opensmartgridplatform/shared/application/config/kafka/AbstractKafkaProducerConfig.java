/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducerConfig<K, V> extends KafkaConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaProducerConfig.class);

  private KafkaTemplate<K, V> kafkaTemplate;

  public AbstractKafkaProducerConfig(
      final Environment environment, final String propertiesPrefix, final String topic) {
    super(environment);

    LOGGER.debug(
        "=================================================================================");
    LOGGER.info("Initializing Kafka Producer for Topic {}", topic);
    LOGGER.debug("Common properties prefix: {}", propertiesPrefix);
    LOGGER.debug("Producer properties prefix: {}", getProducerPropertiesPrefix(propertiesPrefix));
    LOGGER.debug(
        "=================================================================================");

    this.initKafkaTemplate(propertiesPrefix, topic);
  }

  public abstract KafkaTemplate<K, V> kafkaTemplate();

  protected KafkaTemplate<K, V> getKafkaTemplate() {
    return this.kafkaTemplate;
  }

  private static String getProducerPropertiesPrefix(final String propertiesPrefix) {
    return propertiesPrefix + ".producer";
  }

  private void initKafkaTemplate(final String propertiesPrefix, final String topic) {
    final Map<String, Object> producerConfigs = this.producerConfigs(propertiesPrefix);
    final KafkaTemplate<K, V> template =
        new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs));
    template.setDefaultTopic(topic);
    this.kafkaTemplate = template;
  }

  private Map<String, Object> producerConfigs(final String propertiesPrefix) {
    final Map<String, Object> properties = this.createCommonProperties(propertiesPrefix);
    KafkaProperties.producerProperties()
        .forEach(
            (k, v) ->
                this.addIfExist(properties, k, getProducerPropertiesPrefix(propertiesPrefix), v));
    return properties;
  }
}
