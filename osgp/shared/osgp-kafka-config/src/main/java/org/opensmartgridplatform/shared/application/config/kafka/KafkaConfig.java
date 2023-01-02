/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import static java.util.stream.Collectors.*;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.springframework.core.env.Environment;

public abstract class KafkaConfig {

  private final Environment environment;

  public KafkaConfig(final Environment environment) {
    this.environment = environment;
  }

  protected Map<String, Object> configDefToProperties(
      final ConfigDef configDef, final String propertiesPrefix) {
    return configDef.configKeys().entrySet().stream()
        .filter(entry -> exists(entry.getKey(), propertiesPrefix))
        .collect(
            toMap(
                Map.Entry::getKey,
                entry -> getValue(entry.getKey(), entry.getValue().type(), propertiesPrefix)));
  }

  protected boolean exists(final String configKey, final String prefix) {
    return StringUtils.isNotEmpty(this.environment.getProperty(prefix + "." + configKey))
        || StringUtils.isNotEmpty(this.environment.getProperty(configKey));
  }

  protected Object getValue(
      final String configName, final ConfigDef.Type configType, final String prefix) {

    final String prefixedValue = this.environment.getProperty(prefix + "." + configName);
    if (StringUtils.isNotEmpty(prefixedValue)) {
      return ConfigDef.parseType(configName, prefixedValue, configType);
    }

    final String value = this.environment.getProperty(configName);
    if (StringUtils.isNotEmpty(value)) {
      return ConfigDef.parseType(configName, value, configType);
    }

    return null;
  }
}
