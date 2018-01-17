/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.config;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.core.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageSender;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.jms.JmsConfiguration;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationNames;
import com.alliander.osgp.shared.application.config.jms.JmsPropertyNames;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-domain-core.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterDomainCore/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    @Qualifier("domainCoreIncomingWebServiceRequestsMessageListener")
    private WebServiceRequestMessageListener webServiceRequestMessageListener;

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreResponsesMessageListener")
    private OsgpCoreResponseMessageListener osgpCoreResponseMessageListener;

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreRequestsMessageListener")
    private OsgpCoreRequestMessageListener osgpCoreRequestMessageListener;

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

    // === JMS SETTINGS: COMMON WEB SERVICE REQUESTS ===

    @Bean
    public JmsConfiguration commonWsRequestsJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration(JmsConfigurationNames.JMS_COMMON_WS_REQUESTS,
                this.webServiceRequestMessageListener);
    }

    @Bean(name = "domainCoreIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer commonWsRequestsMessageListenerContainer(
            final JmsConfiguration commonWsRequestsJmsConfiguration) {
        return commonWsRequestsJmsConfiguration.getMessageListenerContainer();
    }

    // === JMS SETTINGS: COMMON WEB SERVICE RESPONSES ===

    @Bean
    public JmsConfiguration commonWsResponsesJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_WS_RESPONSES);
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate commonWsResponsesJmsTemplate(final JmsConfiguration commonWsResponsesJmsConfiguration) {
        return commonWsResponsesJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesMessageSender")
    public WebServiceResponseMessageSender commonWsResponsesMessageSender() {
        return new WebServiceResponseMessageSender();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE REQUESTS ===

    @Bean
    public JmsConfiguration osgpCoreRequestsJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_OSGP_CORE_REQUESTS);
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate osgpCoreRequestsJmsTemplate(final JmsConfiguration osgpCoreRequestsJmsConfiguration) {
        return osgpCoreRequestsJmsConfiguration.getJmsTemplate();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE RESPONSES ===

    @Bean
    public JmsConfiguration osgpCoreResponsesJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration(JmsConfigurationNames.JMS_OSGP_CORE_RESPONSES,
                this.osgpCoreResponseMessageListener);
    }

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreResponsesMessageListenerContainer(
            final JmsConfiguration osgpCoreResponsesJmsConfiguration) {
        return osgpCoreResponsesJmsConfiguration.getMessageListenerContainer();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING REQUESTS ===

    @Bean
    public JmsConfiguration osgpCoreRequestsIncomingJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration(
                JmsConfigurationNames.JMS_OSGP_CORE_REQUESTS_INCOMING, this.osgpCoreRequestMessageListener);
    }

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreRequestsIncomingMessageListenerContainer(
            final JmsConfiguration osgpCoreRequestsIncomingJmsConfiguration) {
        return osgpCoreRequestsIncomingJmsConfiguration.getMessageListenerContainer();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING RESPONSES ===

    @Bean
    public JmsConfiguration osgpCoreResponsesIncomingJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_OSGP_CORE_RESPONSES_INCOMING);
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate osgpCoreResponsesIncomingJmsTemplate(
            final JmsConfiguration osgpCoreResponsesIncomingJmsConfiguration) {
        return osgpCoreResponsesIncomingJmsConfiguration.getJmsTemplate();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE TO CORE WEB SERVICE REQUESTS ===

    @Bean
    public JmsConfiguration commonDomainToWsRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_COMMON_DOMAIN_TO_WS_REQUESTS);
    }

    @Bean(name = "domainCoreWebServiceRequestsJmsTemplate")
    public JmsTemplate commonDomainToWsRequestsJmsTemplate(
            final JmsConfiguration commonDomainToWsRequestsJmsConfiguration) {
        return commonDomainToWsRequestsJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "domainCoreWebServiceRequestsMessageSender")
    public WebServiceRequestMessageSender commonDomainToWsRequestsMessageSender() {
        return new WebServiceRequestMessageSender();
    }
}
