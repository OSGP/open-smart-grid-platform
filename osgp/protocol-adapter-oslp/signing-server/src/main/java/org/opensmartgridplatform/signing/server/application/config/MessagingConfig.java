/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.application.config;

import javax.annotation.Resource;
import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.signing.server.infra.messaging.SigningServerResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:signing-server.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SigningServer/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    @Resource
    private Environment environment;

    @Autowired
    @Qualifier("signingServerRequestsMessageListener")
    private MessageListener requestsMessageListener;

    // === JMS SETTINGS ===

    @Override
    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double
                .parseDouble(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS: SIGNING SERVER REQUESTS ===

    @Bean
    public JmsConfiguration incomingSigningServerRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.signing.server.requests",
                this.requestsMessageListener);
    }

    @Bean("signingServerRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingSigningServerRequestsMessageListenerContainer(
            final JmsConfiguration incomingSigningServerRequestsJmsConfiguration) {
        return incomingSigningServerRequestsJmsConfiguration.getMessageListenerContainer();
    }

    // === JMS SETTINGS: SIGNING SERVER RESPONSES ===

    @Bean
    public JmsConfiguration outgoingSigningServerResponsesJmsConfiguration(
            final JmsConfigurationFactory outgoingSigningServerResponsesJmsConfigurationFactory) {
        return outgoingSigningServerResponsesJmsConfigurationFactory
                .initializeConfiguration("jms.signing.server.responses");
    }

    @Bean("signingServerResponseMessageSender")
    public JmsTemplate outgoingSigningServerResponsesJmsTemplate(
            final JmsConfiguration outgoingSigningServerResponsesJmsConfiguration) {
        return outgoingSigningServerResponsesJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public SigningServerResponseMessageSender responseMessageSender() {
        return new SigningServerResponseMessageSender();
    }
}
