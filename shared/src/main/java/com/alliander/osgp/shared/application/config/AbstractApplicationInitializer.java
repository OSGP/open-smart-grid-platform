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

    protected Logger logger;
    private Class<?> contextClass;
    private String logConfig;

    /**
     * 
     * @param contextClass
     * @param logConfig
     */
    public AbstractApplicationInitializer(final Class<?> contextClass, final String logConfig) {
        this.contextClass = contextClass;
        this.logConfig = logConfig;
        this.logger = LoggerFactory.getLogger(this.contextClass);
    }

    protected void startUp(final ServletContext servletContext) throws ServletException {
        // Force the timezone of application to UTC (required for
        // Hibernate/JDBC)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        initializeLogging(servletContext);

        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(this.contextClass);

        servletContext.addListener(new ContextLoaderListener(rootContext));
    }

    private void initializeLogging(final ServletContext servletContext) throws ServletException {
        boolean reinitLogback = false;

        Context initialContext;
        try {
            initialContext = new InitialContext();
            final String logLocation = (String) initialContext.lookup(this.logConfig);

            LogbackConfigurer.initLogging(logLocation);
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            logger.info("Using default logback.xml from classpath. Message [" + e.getMessage() + "]");
            // For some reason it might be the case that the LogbackConfigurer
            // is initialized at this point
            // but the logger will not work in that case. The following is a
            // trigger to
            // reinitialize the logging using the classpath logback.xml file.
            reinitLogback = true;
        }

        if (reinitLogback) {
            // Reinitialize the logback functionality using the internal
            // logback.xml file.
            try {
                LogbackConfigurer.initLogging("classpath:logback.xml");
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
