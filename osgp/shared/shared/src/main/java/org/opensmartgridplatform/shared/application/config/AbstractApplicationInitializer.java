/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.io.File;
import java.io.FileNotFoundException;
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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Web application Java configuration class.
 */
public abstract class AbstractApplicationInitializer {

    protected final Logger logger;
    private final Class<?> contextClass;
    private final String logConfig;
    protected AnnotationConfigWebApplicationContext rootContext;

    /**
     * Constructs instance of ApplicationInitializer
     *
     * @param contextClass
     *            the class holding application specific Spring
     *            ApplicationContext
     * @param logConfig
     *            jndi property which points to logback configuration
     */
    public AbstractApplicationInitializer(final Class<?> contextClass, final String logConfig) {
        this.contextClass = contextClass;
        this.logger = LoggerFactory.getLogger(this.contextClass);
        this.logConfig = logConfig;
        this.rootContext = new AnnotationConfigWebApplicationContext();
    }

    /**
     * Default startup of application context which: - Forces timezone to UTC -
     * Initializes the application logging - Registers the application context
     * with ServletContext
     *
     * @param servletContext
     *            Java servlet context as supplied by application server
     * @throws ServletException
     *             thrown when a servlet encounters difficulty
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
        Context initialContext;
        String customLogLocation;
        final String defaultLogLocation = "classpath:logback-config.xml";

        try {
            initialContext = new InitialContext();
            customLogLocation = (String) initialContext.lookup(this.logConfig);

            // Load specific logback configuration, otherwise fallback to
            // classpath logback.xml
            if (new File(customLogLocation).exists()) {
                this.initializeLoggingContext(customLogLocation);
            } else {
                this.initializeLoggingContext(defaultLogLocation);
            }
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            this.logger.info("Failed to initialize logging using {} or {}", this.logConfig, defaultLogLocation, e);
            throw new ServletException(e);
        }
    }

    private void initializeLoggingContext(final String location) throws FileNotFoundException, JoranException {
        final LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();

        // in the current version logback automatically configures at
        // startup the context, so we have to reset it
        loggerContext.reset();

        // reinitialize the logger context. calling this method allows
        // configuration through groovy or xml
        new ContextInitializer(loggerContext).configureByResource(ResourceUtils.getURL(location));
        this.logger.info("Initialized logging using {}", location);
    }
}
