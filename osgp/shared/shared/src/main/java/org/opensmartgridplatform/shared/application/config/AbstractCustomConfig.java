//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
    final PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
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
  protected void addPropertySource(final String location, final boolean ignoreResourceNotFound)
      throws IOException {
    final MutablePropertySources propertySources = ENVIRONMENT.getPropertySources();
    final String locationNoPlaceholders = ENVIRONMENT.resolvePlaceholders(location);

    try {
      propertySources.addLast(new ResourcePropertySource(locationNoPlaceholders));
    } catch (final IOException e) {
      if (!ignoreResourceNotFound) {
        throw e;
      }
    }
  }
}
