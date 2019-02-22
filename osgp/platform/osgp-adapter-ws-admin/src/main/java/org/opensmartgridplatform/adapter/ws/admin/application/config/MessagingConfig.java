/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.config;

import org.apache.activemq.RedeliveryPolicy;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminResponseMessageFinder;
import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.jms.JmsPropertyNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    public static final String PROPERTY_NAME_JMS_RECEIVE_TIMEOUT = "jms.admin.responses.receive.timeout";

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

    // === JMS SETTINGS: ADMIN REQUESTS ===

    @Bean
    public JmsConfiguration wsAdminOutgoingRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JSM_ADMIN_REQUESTS);
    }

    @Bean(name = "wsAdminOutgoingRequestsJmsTemplate")
    public JmsTemplate wsAdminOutgoingRequestsJmsTemplate(
            final JmsConfiguration wsAdminOutgoingRequestsJmsConfiguration) {
        return wsAdminOutgoingRequestsJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public AdminRequestMessageSender adminRequestMessageSender() {
        return new AdminRequestMessageSender();
    }

    // === JMS SETTINGS: ADMIN RESPONSES ===

    @Bean
    public JmsConfiguration wsAdminIncomingResponsesJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_ADMIN_RESPONSES);
    }

    @Bean(name = "wsAdminIncomingResponsesJmsTemplate")
    public JmsTemplate wsAdminIncomingResponsesJmsTemplate(
            final JmsConfiguration wsAdminIncomingResponsesJmsConfiguration) {
        final Long receiveTimeout = Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_RECEIVE_TIMEOUT));

        final JmsTemplate jmsTemplate = wsAdminIncomingResponsesJmsConfiguration.getJmsTemplate();
        jmsTemplate.setReceiveTimeout(receiveTimeout);
        return jmsTemplate;
    }

    @Bean
    public AdminResponseMessageFinder adminResponseMessageFinder() {
        return new AdminResponseMessageFinder();
    }

    // === JMS SETTINGS: ADMIN LOGGING ===

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
}
