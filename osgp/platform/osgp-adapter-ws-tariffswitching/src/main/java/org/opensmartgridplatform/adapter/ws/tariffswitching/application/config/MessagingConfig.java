/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.jms.JmsPropertyNames;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-tariffswitching.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsTariffSwitching/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    public static final String PROPERTY_NAME_JMS_RECEIVE_TIMEOUT = "jms.tariffswitching.responses.receive.timeout";

    @Resource
    private Environment environment;

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

    // === JMS SETTINGS: TARIFF SWITCHING REQUESTS ===

    @Bean
    public JmsConfiguration tariffSwitchingRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_TARIFFSWITCHING_REQUESTS);
    }

    @Bean(name = "wsTariffSwitchingOutgoingRequestsJmsTemplate")
    public JmsTemplate tariffSwitchingRequestsJmsTemplate(
            final JmsConfiguration tariffSwitchingRequestsJmsConfiguration) {
        return tariffSwitchingRequestsJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender() {
        return new TariffSwitchingRequestMessageSender();
    }

    // === JMS SETTINGS: TARIFF SWITCHING RESPONSES ===

    @Bean
    public JmsConfiguration tariffSwitchingResponsesJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory,
            final TariffSwitchingResponseMessageListener tariffSwitchingResponseMessageListener) {
        final JmsConfiguration jmsConfiguration = jmsConfigurationFactory.initializeConfiguration(
                JmsConfigurationNames.JMS_TARIFFSWITCHING_RESPONSES, tariffSwitchingResponseMessageListener);
        // Some message need to be consumed by message listener container.
        // This is defined by a message selector string.
        //
        // All other messages will be retrieved by {@link
        // MessagingConfig#publicLightingResponseMessageFinder()}.
        jmsConfiguration.getMessageListenerContainer().setMessageSelector("JMSType = 'SET_TARIFF_SCHEDULE'");
        return jmsConfiguration;
    }

    @Bean(name = "wsTariffSwitchingIncomingResponsesJmsTemplate")
    public JmsTemplate tariffSwitchingResponsesJmsTemplate(
            final JmsConfiguration tariffSwitchingResponsesJmsConfiguration) {
        final Long receiveTimeout = Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_RECEIVE_TIMEOUT));

        final JmsTemplate jmsTemplate = tariffSwitchingResponsesJmsConfiguration.getJmsTemplate();
        jmsTemplate.setReceiveTimeout(receiveTimeout);
        return jmsTemplate;
    }

    @Bean(name = "wsTariffSwitchingResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer tariffSwitchingResponseMessageListenerContainer(
            final JmsConfiguration tariffSwitchingResponsesJmsConfiguration) {
        return tariffSwitchingResponsesJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    @Qualifier("domainTariffSwitchingResponseMessageProcessorMap")
    public MessageProcessorMap tariffSwitchingResponseMessageProcessorMap() {
        return new BaseMessageProcessorMap("domainResponseMessageProcessorMap");
    }

    @Bean
    public TariffSwitchingResponseMessageListener tariffSwitchingResponseMessageListener() {
        return new TariffSwitchingResponseMessageListener();
    }

    @Bean(name = "wsTariffSwitchingIncomingResponsesMessageFinder")
    public TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder() {
        return new TariffSwitchingResponseMessageFinder();
    }

    // === JMS SETTINGS: TARIFF SWITCHING LOGGING ===

    @Bean
    public JmsConfiguration loggingJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_TARIFFSWITCHING_LOGGING);
    }

    @Bean
    public JmsTemplate loggingJmsTemplate(final JmsConfiguration loggingJmsConfiguration) {
        return loggingJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }
}
