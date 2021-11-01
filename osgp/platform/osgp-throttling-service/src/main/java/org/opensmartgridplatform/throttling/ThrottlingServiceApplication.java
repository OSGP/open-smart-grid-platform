/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TimeZone;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

@SpringBootApplication(
    exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@PropertySource("classpath:osgp-throttling-service.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/ThrottlingService/config}", ignoreResourceNotFound = true)
public class ThrottlingServiceApplication extends SpringBootServletInitializer {

  public static void main(final String[] args) {
    /*
     * Main method for running the ThrottlingServiceApplication outside of a servlet container.
     * Note that this will use configuration from the classpath, as the environment resources from
     * context.xml are not defined.
     */
    SpringApplication.run(ThrottlingServiceApplication.class);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    this.initializeLogging();
    return builder;
  }

  private void initializeLogging() {
    String logConfigLocation = null;
    try {
      logConfigLocation =
          (String) new InitialContext().lookup("java:comp/env/osgp/ThrottlingService/log-config");
      if (Files.exists(Paths.get(logConfigLocation))) {
        final LoggerContext loggerContext =
            (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
        loggerContext.reset();
        new ContextInitializer(loggerContext)
            .configureByResource(ResourceUtils.getURL(logConfigLocation));
      }
    } catch (final NamingException e) {
      this.logger.warn(
          "NamingException initializing logging, staying with config by logback-spring.xml on classpath.",
          e);
    } catch (final FileNotFoundException e) {
      this.logger.warn(
          String.format(
              "Configured log file (%s) not found, staying with config by logback-spring.xml on classpath.",
              logConfigLocation));
    } catch (final JoranException e) {
      this.logger.error(
          String.format("Exception initializing logger context from %s", logConfigLocation), e);
    }
  }
}
