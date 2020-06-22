package org.opensmartgridplatform.secretmgmt.application.config;

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
            logger.info("Failed to initialize logging using {}", LOG_CONFIG, e);
            throw new ServletException(e);
        }
    }

}
