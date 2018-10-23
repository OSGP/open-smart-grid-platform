/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageListener;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.jms.JmsPropertyNames;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-ws-core.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterWsCore/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    public static final String PROPERTY_NAME_JMS_RECEIVE_TIMEOUT = "jms.common.responses.receive.timeout";

    @Resource
    private Environment environment;

    @Qualifier("domainCoreToWsIncomingWebServiceRequestsMessageListener")
    @Autowired
    private CommonRequestMessageListener commonRequestMessageListener;

    // === JMS SETTINGS ===

    @Override
    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(JmsPropertyNames.PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(
                this.environment.getRequiredProperty(JmsPropertyNames.PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(JmsPropertyNames.PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(JmsPropertyNames.PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS: COMMON REQUESTS ===

    @Bean
    public JmsConfiguration commonRequestsJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_REQUESTS);
    }

    @Bean(name = "wsCoreOutgoingRequestsJmsTemplate")
    public JmsTemplate commonRequestsJmsTemplate(final JmsConfiguration commonRequestsJmsConfiguration) {
        return commonRequestsJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public CommonRequestMessageSender commonRequestMessageSender() {
        return new CommonRequestMessageSender();
    }

    // === JMS SETTINGS: COMMON RESPONSES ===

    @Bean
    public JmsConfiguration commonResponsesJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_RESPONSES);
    }

    @Bean(name = "wsCoreIncomingResponsesJmsTemplate")
    public JmsTemplate commonResponsesJmsTemplate(final JmsConfiguration commonResponsesJmsConfiguration) {
        final Long receiveTimeout = Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_RECEIVE_TIMEOUT));

        final JmsTemplate jmsTemplate = commonResponsesJmsConfiguration.getJmsTemplate();
        jmsTemplate.setReceiveTimeout(receiveTimeout);
        return jmsTemplate;
    }

    @Bean(name = "wsCoreIncomingResponsesMessageFinder")
    public CommonResponseMessageFinder commonResponseMessageFinder() {
        return new CommonResponseMessageFinder();
    }

    // === JMS SETTINGS: COMMON LOGGING ===

    @Bean
    public JmsConfiguration loggingJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_LOGGING);
    }

    @Bean
    public JmsTemplate loggingJmsTemplate(final JmsConfiguration loggingJmsConfiguration) {
        return loggingJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

    // === JMS SETTINGS: REQUESTS FROM COMMON DOMAIN TO COMMON WEB SERVICE ===

    @Bean
    public JmsConfiguration commonRequestsFromDomainToWsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_DOMAIN_TO_WS_REQUESTS,
                this.commonRequestMessageListener);
    }

    @Bean(name = "domainCoreIncomingDomainCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer domainCoreRequestsIncomingMessageListenerContainer(
            final JmsConfiguration commonRequestsFromDomainToWsJmsConfiguration) {
        return commonRequestsFromDomainToWsJmsConfiguration.getMessageListenerContainer();
    }
}
