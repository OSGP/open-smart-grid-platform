/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.config;

import org.opensmartgridplatform.adapter.domain.da.infra.jms.core.OsgpCoreRequestMessageListener;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.core.OsgpCoreResponseMessageListener;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.WebServiceRequestMessageListener;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * An application context Java configuration class.
 */
@Configuration
@PropertySource("classpath:osgp-adapter-domain-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainDistributionAutomation/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    @Qualifier("domainDistributionAutomationIncomingWebServiceRequestMessageListener")
    private WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    @Autowired
    @Qualifier("domainDistributionAutomationIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    @Autowired
    @Qualifier("domainDistributionAutomationIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    // JMS SETTINGS: INCOMING WEB SERVICE REQUESTS ===

    @Bean
    public JmsConfiguration incomingWebServiceJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.incoming.ws.requests",
                this.incomingWebServiceRequestMessageListener);
    }

    @Bean(name = "domainDistributionAutomationIncomingWebServiceRequestMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer(
            final JmsConfiguration incomingWebServiceJmsConfiguration) {
        return incomingWebServiceJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    @Qualifier("domainDistributionAutomationWebServiceRequestMessageProcessorMap")
    public MessageProcessorMap incomingWebServiceRequestMessageProcessorMap() {
        return new BaseMessageProcessorMap("WebServiceRequestMessageProcessorMap");
    }

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES

    @Bean
    JmsConfiguration outgoingWebServiceJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.ws.responses");
    }

    @Bean(name = "domainDistributionAutomationOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate(
            final JmsConfiguration outgoingWebServiceJmsConfiguration) {
        return outgoingWebServiceJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "domainDistributionAutomationOutgoingWebServiceResponseMessageSender")
    public WebServiceResponseMessageSender outgoingWebServiceResponseMessageSender() {
        return new WebServiceResponseMessageSender();
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)

    @Bean
    public JmsConfiguration outgoingOsgpCoreRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.osgp.core.requests");
    }

    @Bean(name = "domainDistributionAutomationOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate(
            final JmsConfiguration outgoingOsgpCoreRequestsJmsConfiguration) {
        return outgoingOsgpCoreRequestsJmsConfiguration.getJmsTemplate();
    }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp
    // core)

    @Bean
    public JmsConfiguration incomingOsgpCoreResponsesJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.incoming.osgp.core.responses",
                this.incomingOsgpCoreResponseMessageListener);
    }

    @Bean(name = "domainDistributionAutomationIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreResponsesMessageListenerContainer(
            final JmsConfiguration incomingOsgpCoreResponsesJmsConfiguration) {
        return incomingOsgpCoreResponsesJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    @Qualifier("domainDistributionAutomationOsgpCoreResponseMessageProcessorMap")
    public MessageProcessorMap incomingOsgpCoreResponseMessageProcessorMap() {
        return new BaseMessageProcessorMap("OsgpCoreResponseMessageProcessorMap");
    }

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp
    // core)

    @Bean
    public JmsConfiguration incomingOsgpCoreRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.incoming.osgp.core.requests",
                this.incomingOsgpCoreRequestMessageListener);
    }

    @Bean(name = "domainDistributionAutomationIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer(
            final JmsConfiguration incomingOsgpCoreRequestsJmsConfiguration) {
        return incomingOsgpCoreRequestsJmsConfiguration.getMessageListenerContainer();
    }

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp
    // core)

    @Bean
    public JmsConfiguration outgoingOsgpCoreResponsesJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.osgp.core.responses");
    }

    @Bean(name = "domainDistributionAutomationOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate(
            final JmsConfiguration outgoingOsgpCoreResponsesJmsConfiguration) {
        return outgoingOsgpCoreResponsesJmsConfiguration.getJmsTemplate();
    }

}
