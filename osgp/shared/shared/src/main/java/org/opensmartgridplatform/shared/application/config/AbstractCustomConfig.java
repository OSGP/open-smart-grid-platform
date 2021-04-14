/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

/** Base class for Configuration classes which allows a customized environment. */
public abstract class AbstractCustomConfig {

  protected static final ConfigurableEnvironment ENVIRONMENT = new StandardServletEnvironment();

  /**
   * Create local property configurer, using local environment.
   *
   * @return instance of {@link PropertySourcesPlaceholderConfigurer}
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
    PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
    ppc.setEnvironment(ENVIRONMENT);
    ppc.setIgnoreResourceNotFound(true);
    ppc.setIgnoreUnresolvablePlaceholders(true);

    return ppc;
  }

  /**
   * Add a property source to the environment. Will always be added last (lowest priority)
   *
   * @param location the location of the property source
   * @param ignoreResourceNotFound indicates whether the source is required
   * @throws IOException when property source is required and not found
   */
  protected void addPropertySource(String location, boolean ignoreResourceNotFound)
      throws IOException {
    MutablePropertySources propertySources = ENVIRONMENT.getPropertySources();
    String locationNoPlaceholders = ENVIRONMENT.resolvePlaceholders(location);

    try {
      propertySources.addLast(new ResourcePropertySource(locationNoPlaceholders));
    } catch (IOException e) {
      if (!ignoreResourceNotFound) {
        throw e;
      }
    }
  }
}
