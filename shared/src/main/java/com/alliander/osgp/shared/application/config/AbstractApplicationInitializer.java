/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import java.io.FileNotFoundException;
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

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;

/**
 * Web application Java configuration class.
 */
public abstract class AbstractApplicationInitializer {

    private static final String DEFAULT_LOGBACK_CONFIG = "classpath:logback.xml";
    
    protected final Logger logger;
    private Class<?> contextClass;
    private String logConfig;
    protected AnnotationConfigWebApplicationContext rootContext;

    /**
     * Constructs instance of ApplicationInitializer 
     * @param contextClass the class holding application specific Spring ApplicationContext
     * @param logConfig jndi property which points to logback configuration
     */
    public AbstractApplicationInitializer(final Class<?> contextClass, final String logConfig) {
        this.contextClass = contextClass;
        this.logConfig = logConfig;
        this.logger = LoggerFactory.getLogger(this.contextClass);
        this.rootContext = new AnnotationConfigWebApplicationContext();
    }

    /**
     * Default startup of application context which:
     * - Forces timezone to UTC
     * - Initializes the application logging
     * - Registers the application context with ServletContext
     * @param servletContext Java servlet context as supplied by application server
     * @throws ServletException
     */
    protected void startUp(final ServletContext servletContext) throws ServletException {
        // Force the timezone of application to UTC (required for Hibernate/JDBC)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        initializeLogging();

        rootContext.register(this.contextClass);

        servletContext.addListener(new ContextLoaderListener(rootContext));
    }

    private void initializeLogging() throws ServletException {
        Context initialContext;
        try {
            initialContext = new InitialContext();
            final String logLocation = (String) initialContext.lookup(this.logConfig);

            LogbackConfigurer.initLogging(logLocation);

            logger.debug("Initialized logging using {}", this.logConfig);
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            logger.debug("Failed to initialize logging using {}, falling back to default logging", this.logConfig, e);

            /*
             * For some reason it might be the case that the LogbackConfigurer
             * is initialized at this point but the logger will not work in that
             * case. 
             * 
             * Fallback to reinitialize the logging using the classpath logback.xml file.
             */
            initializeDefaultLogging(DEFAULT_LOGBACK_CONFIG);
        }
    }
    
    private void initializeDefaultLogging(final String defaultConfig) throws ServletException {
        try {
            LogbackConfigurer.initLogging(defaultConfig);
            logger.debug("Initialized default logging using {}", defaultConfig);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
