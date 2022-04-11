/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.TimeZone;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/** Web application Java configuration class. */
public abstract class AbstractApplicationInitializer {

  private static final String DEFAULT_LOG_LOCATION = "classpath:logback-config.xml";
  private static final String GLOBAL_LOG_JNDI_NAME = "java:/comp/env/osgp/Global/log-config";

  protected final Logger logger;
  private final Class<?> contextClass;
  private final String logConfig;
  protected final AnnotationConfigWebApplicationContext rootContext;

  /**
   * Constructs instance of ApplicationInitializer
   *
   * @param contextClass the class holding application specific Spring ApplicationContext
   * @param logConfig jndi property which points to logback configuration
   */
  protected AbstractApplicationInitializer(final Class<?> contextClass, final String logConfig) {
    this.contextClass = contextClass;
    this.logger = LoggerFactory.getLogger(this.contextClass);
    this.logConfig = logConfig;
    this.rootContext = new AnnotationConfigWebApplicationContext();
  }

  /**
   * Default startup of application context which: - Forces timezone to UTC - Initializes the
   * application logging - Registers the application context with ServletContext
   *
   * @param servletContext Java servlet context as supplied by application server
   * @throws ServletException thrown when a servlet encounters difficulty
   */
  protected void startUp(final ServletContext servletContext) throws ServletException {
    // Force the timezone of application to UTC (required for
    // Hibernate/JDBC)
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.initializeLogging();

    this.rootContext.register(this.contextClass);

    servletContext.addListener(new ContextLoaderListener(this.rootContext));
  }

  private void initializeLogging() throws ServletException {
    try {
      final InitialContext initialContext = new InitialContext();
      final Optional<String> customLogLocation =
          this.getLogConfigLocation(this.logConfig, initialContext);
      final Optional<String> globalLogLocation =
          this.getLogConfigLocation(GLOBAL_LOG_JNDI_NAME, initialContext);

      final String logLocation =
          customLogLocation.orElse(globalLogLocation.orElse(DEFAULT_LOG_LOCATION));
      this.initializeLoggingContext(logLocation);
    } catch (final NamingException | FileNotFoundException | JoranException e) {
      throw new ServletException("Failed to initialize logging", e);
    }
  }

  /**
   * Get a log config location based on the given JNDI name. If the file referenced by the JNDI
   * entry exist, the location will be returned.
   *
   * @param jndiName JNDI name where a location could be found
   * @param initialContext Servlet context
   * @return Optional String: present if a file exists in the referenced location, empty otherwise
   */
  private Optional<String> getLogConfigLocation(
      final String jndiName, final Context initialContext) {
    try {
      final String location = (String) initialContext.lookup(jndiName);
      if (new File(location).exists()) {
        this.logger.debug("Using log config {} found through JNDI name {}", location, jndiName);
        return Optional.of(location);
      } else {
        return Optional.empty();
      }
    } catch (final NamingException e) {
      // The GLOBAL_LOG_JNDI_NAME is optional. If not defined in the ServletContext, a
      // NamingException will occur. No problem.
      this.logger.trace(
          "Error performing JNDI lookup for {}, probably an unknown name.", jndiName, e);
      return Optional.empty();
    }
  }

  private void initializeLoggingContext(final String location)
      throws FileNotFoundException, JoranException {
    final LoggerContext loggerContext =
        (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();

    // in the current version logback automatically configures at
    // startup the context, so we have to reset it
    loggerContext.reset();

    // reinitialize the logger context. calling this method allows
    // configuration through groovy or xml
    new ContextInitializer(loggerContext).configureByResource(ResourceUtils.getURL(location));
    this.logger.info("Initialized logging using {}", location);
  }
}
