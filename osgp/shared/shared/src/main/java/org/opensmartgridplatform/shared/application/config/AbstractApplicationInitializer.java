/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.io.File;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import ch.qos.logback.classic.util.ContextInitializer;

/**
 * Web application Java configuration class.
 */
public abstract class AbstractApplicationInitializer {

    protected Logger logger;
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
        String logLocation;

        try {
            initialContext = new InitialContext();
            logLocation = (String) initialContext.lookup(this.logConfig);
        } catch (final NamingException e) {
            this.logger = LoggerFactory.getLogger(this.contextClass);
            this.logger.info("Failed to initialize logging using {}", this.logConfig, e);
            throw new ServletException(e);
        }

        // Load specific logback configuration, otherwise fallback to classpath
        // logback.xml
        // The system property should be set BEFORE initialization of the
        // logging system;
        // therefore this unusual notation of not setting the logger in fa the
        // constructor.
        final boolean logLocationExists = new File(logLocation).exists();
        if (logLocationExists) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logLocation);
        }

        this.logger = LoggerFactory.getLogger(this.contextClass);

        if (logLocationExists) {
            this.logger.info("Initialized logging using {} ({})", this.logConfig, logLocation);
        } else {
            this.logger.info("{} not found", logLocation);
        }

        // Remove the property, to be sure other applications won't be using it.
        // After some testing, it as found that it might need a Thread.sleep
        // because it seems like the logging system is not fully initialized
        // before the first log line has been written.
        // Although as seen from
        // http://logback.qos.ch/manual/configuration.html#configFileProperty
        // the sleep should not be needed.
        System.clearProperty(ContextInitializer.CONFIG_FILE_PROPERTY);
    }
}
