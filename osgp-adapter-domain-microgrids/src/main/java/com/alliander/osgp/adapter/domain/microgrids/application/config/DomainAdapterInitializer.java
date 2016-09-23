/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.application.config;

import java.io.FileNotFoundException;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;

/**
 * Web application Java configuration class. The usage of web application
 * initializer requires Spring Framework 3.1 and Servlet 3.0.
 */
public class DomainAdapterInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        try {
            // Force the timezone of application to UTC (required for
            // Hibernate/JDBC)
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            final Context initialContext = new InitialContext();

            final String logLocation = (String) initialContext
                    .lookup("java:comp/env/osp/osgpAdapterDomainMicrogrids/log-config");
            LogbackConfigurer.initLogging(logLocation);

            final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
            rootContext.register(ApplicationContext.class);

            servletContext.addListener(new ContextLoaderListener(rootContext));

        } catch (final NamingException e) {
            throw new ServletException("naming exception", e);
        } catch (final FileNotFoundException e) {
            throw new ServletException("Logging file not found", e);
        } catch (final JoranException e) {
            throw new ServletException("Logback exception", e);
        }
    }
}
