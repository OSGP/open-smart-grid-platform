// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/** Base class for Configuration classes. */
public abstract class AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfig.class);

  /** Qualifier to detect a class path type resource */
  private static final String CLASS_PATH_QUALIFIER = "class path resource [";

  /** Qualifier to detect a file type resource (i.e. /etc/osp/xxx.properties) */
  private static final String RESOURCE_PATH_QUALIFIER = "URL [file:";

  /**
   * Qualifier to detect a specific global.properties file resource (i.e.
   * /etc/osp/global.properties)
   */
  private static final String RESOURCE_GLOBAL_PATH_QUALIFIER = "global.properties";

  /** Standard spring environment (autowired with setter) */
  protected Environment environment;

  /**
   * Default implementation to resolve ${} values in annotations.
   *
   * @return static PropertySourcesPlaceholderConfigurer
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
    final PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
    ppc.setIgnoreUnresolvablePlaceholders(true);
    return ppc;
  }

  /**
   * Special setter for Spring environment, which reorders the property sources in defined order
   * (high to lowest priority): - environment config - local config files - global config files -
   * classpath config files
   *
   * @param configurableEnvironment Spring environment
   */
  @Autowired
  public void setConfigurableEnvironment(final ConfigurableEnvironment configurableEnvironment) {
    this.environment = configurableEnvironment;
    reorderEnvironment(configurableEnvironment);
  }

  private static void reorderEnvironment(final ConfigurableEnvironment configurableEnvironment) {
    final List<PropertySource<?>> env = new ArrayList<>();
    final List<PropertySource<?>> file = new ArrayList<>();
    final List<PropertySource<?>> global = new ArrayList<>();
    final List<PropertySource<?>> classpath = new ArrayList<>();

    final MutablePropertySources sources = configurableEnvironment.getPropertySources();

    // Divide property sources in groups
    for (final PropertySource<?> source : sources) {
      if (source.getName().contains(RESOURCE_GLOBAL_PATH_QUALIFIER)) {
        global.add(source);
      } else if (source.getName().startsWith(RESOURCE_PATH_QUALIFIER)) {
        file.add(source);
      } else if (source.getName().startsWith(CLASS_PATH_QUALIFIER)) {
        classpath.add(source);
      } else {
        env.add(source);
      }
    }

    // Re-add all property sources in correct priority order
    addSources(sources, env);
    addSources(sources, file);
    addSources(sources, global);
    addSources(sources, classpath);
  }

  private static void addSources(
      final MutablePropertySources sources, final List<PropertySource<?>> sourcesToAdd) {
    // Remove and add each source
    for (final PropertySource<?> source : sourcesToAdd) {
      sources.remove(source.getName());
      sources.addLast(source);
    }
  }

  protected Integer getNonRequiredIntegerPropertyValue(
      final String propertyName, final Integer defaultValue) {
    final String propertyValue = this.environment.getProperty(propertyName);
    if (StringUtils.isEmpty(propertyValue)) {
      LOGGER.info(
          "No value found for property with name: {}, using default value: {}",
          propertyName,
          defaultValue);
      return defaultValue;
    } else {
      final Integer integerValue = Integer.valueOf(propertyValue);
      LOGGER.info("Value found for property with name: {}, value: {}", propertyName, integerValue);
      return integerValue;
    }
  }
}
