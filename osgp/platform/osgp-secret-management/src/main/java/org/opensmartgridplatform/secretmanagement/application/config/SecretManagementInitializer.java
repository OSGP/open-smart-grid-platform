/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.io.File;
import java.io.FileNotFoundException;

/* The responsibility of this class is to configure logback logging using a context property
 * provided by the application server (context.xml).
 */
public class SecretManagementInitializer implements WebApplicationInitializer {

    private static final String LOG_CONFIG = "java:comp/env/osgp/SecretManagement/log-config";

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {

        Logger logger = LoggerFactory.getLogger(SecretManagementInitializer.class);
        Context initialContext;
        try {
            initialContext = new InitialContext();
            final String logLocation = (String) initialContext.lookup(LOG_CONFIG);

            // Load specific logback configuration, otherwise fallback to
            // classpath logback.xml
            if (new File(logLocation).exists()) {
                LogbackConfigurer.initLogging(logLocation);
                logger.info("Initialized logging using {}", LOG_CONFIG);
            }
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            throw new ServletException("Failed to initialize logging using " + LOG_CONFIG, e);
        }
    }
}
