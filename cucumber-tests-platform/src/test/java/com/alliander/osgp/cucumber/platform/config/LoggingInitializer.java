package com.alliander.osgp.cucumber.platform.config;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.context.annotation.Configuration;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;

@Configuration
public class LoggingInitializer {
    public LoggingInitializer() throws FileNotFoundException, JoranException {
        final String logLocation = "/etc/osp/test/logback.xml";
        if (new File(logLocation).exists()) {
            LogbackConfigurer.initLogging(logLocation);
        }
    }
}
