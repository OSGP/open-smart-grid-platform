/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;

public class KafkaConfig {

  private final Environment environment;

  public KafkaConfig(final Environment environment) {
    this.environment = environment;
  }

  protected Map<String, Object> createCommonProperties(final String propertiesPrefix) {
    final Map<String, Object> properties = new HashMap<>();
    KafkaProperties.commonProperties()
        .forEach((k, v) -> this.addIfExist(properties, k, propertiesPrefix, v));
    return properties;
  }

  protected <T> void addIfExist(
      final Map<String, Object> properties,
      final String kafkaProperty,
      final String prefix,
      final Class<T> targetType) {
    final String fullPropertyName = prefix + "." + kafkaProperty;
    final T value = this.environment.getProperty(fullPropertyName, targetType);
    if (value != null) {
      properties.put(kafkaProperty, value);
    }
  }
}
