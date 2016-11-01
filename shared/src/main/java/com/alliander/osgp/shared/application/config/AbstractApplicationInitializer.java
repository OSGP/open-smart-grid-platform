/**
 * Copyright 2015 Smart Society Services B.V.
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
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;

/**
 * Web application Java configuration class.
 */
public class AbstractApplicationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
    
    public void startUp(final ServletContext servletContext, final String logConfig) throws ServletException {
        
        boolean reinitLogback = false;
        
        // Force the timezone of application to UTC (required for
        // Hibernate/JDBC)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Context initialContext;
        try {
            initialContext = new InitialContext();
            
            // Try to load the logback from the context.xml. Keep in mind that the 
            // key might not exist in context.xml or it might exist, but the file doesn't exist.
            final String logLocation = (String) initialContext
                    .lookup(logConfig);
            
            LogbackConfigurer.initLogging(logLocation);
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            LOGGER.info("Using default logback.xml from classpath. Message [" + e.getMessage() + "]");
        
            // For some reason it might be the case that the LogbackConfigurer is initialized at this point
            // but the logger will not work in that case. The following is a trigger to 
            // reinitialize the logging using the classpath logback.xml file.
            reinitLogback = true;
        }
        
        if (reinitLogback) {
            // Reinitialize the logback functionality using the internal logback.xml file.
            try {
                LogbackConfigurer.initLogging("classpath:logback.xml");
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationContext.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));
    }
}
