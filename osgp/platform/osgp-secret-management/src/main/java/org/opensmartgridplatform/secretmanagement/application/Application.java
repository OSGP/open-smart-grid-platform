/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@SpringBootApplication(
    exclude = {
      SecurityAutoConfiguration.class,
      ManagementWebSecurityAutoConfiguration.class,
      DataSourceAutoConfiguration.class
    })
@PropertySource("classpath:osgp-secret-management.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SecretManagement/config}", ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"org.opensmartgridplatform.secretmanagement"})
public class Application extends SpringBootServletInitializer {

  private static final String LOG_CONFIG = "java:comp/env/osgp/SecretManagement/log-config";

  public static void main(final String[] args) {
    // command line startup method (not used when app is started in application server)
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    final Optional<String> logPropertiesLocation = this.getLogbackConfigurationLocation();

    if (logPropertiesLocation.isPresent()) {
      log.info("Location for properties: {}", logPropertiesLocation.get());

      final Properties props = new Properties();
      props.setProperty("logging.config", logPropertiesLocation.get());
      builder.application().setDefaultProperties(props);
    }

    return builder.sources(Application.class);
  }

  private Optional<String> getLogbackConfigurationLocation() {
    try {
      final Context initialContext = new InitialContext();
      final String location = (String) initialContext.lookup(LOG_CONFIG);
      final File logConfig = new File(location);

      if (logConfig.exists()) {
        return Optional.of(location);
      }

      log.error("File {} does not exist.", location);
    } catch (final NamingException | RuntimeException e) {
      log.error(
          "Getting the location of the logback configuration file failed. Reason: {}",
          e.getMessage(),
          e);
    }

    return Optional.empty();
  }
}
