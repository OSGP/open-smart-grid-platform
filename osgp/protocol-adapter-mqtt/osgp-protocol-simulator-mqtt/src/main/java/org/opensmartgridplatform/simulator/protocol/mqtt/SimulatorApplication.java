/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.StandardServletEnvironment;

@Configuration
@EnableScheduling
@SpringBootApplication
@PropertySource(value = "file:${osgp/SimulatorProtocolMqtt/config}", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:osgp-simulator-protocol-mqtt.properties", ignoreResourceNotFound = true)
public class SimulatorApplication extends SpringBootServletInitializer {

    protected static final ConfigurableEnvironment ENVIRONMENT = new StandardServletEnvironment();

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        final PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setEnvironment(ENVIRONMENT);
        ppc.setIgnoreResourceNotFound(true);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(SimulatorApplication.class);
    }

    public static void main(final String[] args) {
        // Force UTC timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(SimulatorApplication.class, args);
    }
}
