/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.opensmartgridplatform.logging.domain.entities.WebServiceMonitorLogItem;
import org.opensmartgridplatform.logging.domain.repositories.WebServiceMonitorLogRepository;
import org.opensmartgridplatform.logging.infra.jms.LoggingMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;

/**
 * An application context Java configuration class.
 */
@Configuration
@ComponentScan(basePackageClasses = { WebServiceMonitorLogItem.class, LoggingMessageListener.class })
@EnableJpaRepositories(basePackageClasses = { WebServiceMonitorLogRepository.class })
@EnableTransactionManagement
@PropertySources({ @PropertySource("classpath:osgp-logging.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/Logging/config}", ignoreResourceNotFound = true), })
@Import({ LoggingConfig.class, ProtocolMessagingConfig.class })
public class ApplicationContext extends AbstractMessagingConfig {

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    @Resource
    private Environment environment;

    // === JMS SETTINGS ===

    @Override
    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));

        return redeliveryPolicy;
    }

    @Bean
    public JmsConfiguration loggingRequestsJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.logging", this.loggingMessageListener());
    }

    @Bean
    public DefaultMessageListenerContainer loggingMessageListenerContainer(
            final JmsConfiguration loggingRequestsJmsConfiguration) {
        return loggingRequestsJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    public LoggingMessageListener loggingMessageListener() {
        return new LoggingMessageListener();
    }
}
