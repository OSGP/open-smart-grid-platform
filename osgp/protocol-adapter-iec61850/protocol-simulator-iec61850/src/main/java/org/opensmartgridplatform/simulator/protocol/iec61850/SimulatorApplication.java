// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.StandardServletEnvironment;

@Configuration
@EnableScheduling
@SpringBootApplication
@PropertySource(
    value = "classpath:osgp-simulator-protocol-iec61850.properties",
    ignoreResourceNotFound = false)
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/SimulatorProtocolIec61850/config}",
    ignoreResourceNotFound = true)
public class SimulatorApplication extends SpringBootServletInitializer {

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

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
    return application.sources(SimulatorApplication.class);
  }

  public static void main(final String[] args) {
    // Force UTC timezone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    SpringApplication.run(SimulatorApplication.class, args);
  }
}
