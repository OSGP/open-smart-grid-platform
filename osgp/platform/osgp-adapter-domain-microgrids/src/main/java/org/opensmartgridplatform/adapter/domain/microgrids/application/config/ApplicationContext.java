/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan("org.opensmartgridplatform.shared.domain.services")
@ComponentScan("org.opensmartgridplatform.domain.core")
@ComponentScan("org.opensmartgridplatform.adapter.domain.microgrids")
@PropertySource("classpath:osgp-adapter-domain-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainMicrogrids/config}", ignoreResourceNotFound = true)
@EnableTransactionManagement
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    @Value("#{T(java.time.Duration).parse('${communication.monitoring.minimum.duration.between.communication.time.updates:PT1M}')}")
    private Duration minimumDurationBetweenCommunicationTimeUpdates;

    @Value("#{T(java.time.Duration).parse('${communication.monitoring.maximum.duration.without.communication:PT5M}')}")
    private Duration maximumDurationWithoutCommunication;

    @Bean
    public Duration minimumDurationBetweenCommunicationTimeUpdates() {
        LOGGER.debug("Initializing bean minimumDurationBetweenCommunicationTimeUpdates with value: {}",
                this.minimumDurationBetweenCommunicationTimeUpdates);
        return this.minimumDurationBetweenCommunicationTimeUpdates;
    }

    @Bean
    public Duration maximumDurationWithoutCommunication() {
        LOGGER.debug("Initializing bean maximumDurationWithoutCommunication with value: {}",
                this.maximumDurationWithoutCommunication);
        return this.maximumDurationWithoutCommunication;
    }
}
