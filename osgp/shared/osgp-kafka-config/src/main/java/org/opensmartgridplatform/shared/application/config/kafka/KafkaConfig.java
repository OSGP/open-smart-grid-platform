// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.kafka;

import static java.util.stream.Collectors.*;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public abstract class KafkaConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);

  private final Environment environment;

  protected KafkaConfig(final Environment environment) {
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

    final String prefixedPropertyName = prefix + "." + configName;

    final String prefixedValue = this.environment.getProperty(prefixedPropertyName);
    if (StringUtils.isNotEmpty(prefixedValue)) {
      LOGGER.trace("Found prefixed property: {} = {}", prefixedPropertyName, prefixedValue);
      return ConfigDef.parseType(configName, prefixedValue, configType);
    }

    final String value = this.environment.getProperty(configName);
    if (StringUtils.isNotEmpty(value)) {
      LOGGER.trace("Found property: {} = {}", configName, prefixedValue);
      return ConfigDef.parseType(configName, value, configType);
    }

    return null;
  }
}
