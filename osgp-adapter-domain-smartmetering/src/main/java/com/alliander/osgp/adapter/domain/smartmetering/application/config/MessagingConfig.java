/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;

/**
 * An application context Java configuration class.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-domain-smartmetering.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterDomainSmartMetering/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    @Qualifier("domainSmartMeteringIncomingWebServiceRequestMessageListener")
    private WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    @Autowired
    @Qualifier("domainSmartMeteringIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    @Autowired
    @Qualifier("domainSmartMeteringIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    @Bean(name = "domainSmartMeteringIncomingWebServiceRequestMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory
                .initializeConfiguration("jms.incoming.ws.requests", this.incomingWebServiceRequestMessageListener)
                .getMessageListenerContainer();
    }

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES
    @Bean(name = "domainSmartMeteringOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.ws.responses").getJmsTemplate();
    }

    @Bean(name = "domainSmartMeteringOutgoingWebServiceResponseMessageSender")
    public WebServiceResponseMessageSender outgoingWebServiceResponseMessageSender() {
        return new WebServiceResponseMessageSender();
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)
    @Bean(name = "domainSmartMeteringOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.osgp.core.requests").getJmsTemplate();
    }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp
    // core)

    @Bean(name = "domainSmartMeteringIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreResponsesMessageListenerContainer(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.incoming.osgp.core.responses",
                this.incomingOsgpCoreResponseMessageListener).getMessageListenerContainer();
    }

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp
    // core)

    @Bean(name = "domainSmartMeteringIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory
                .initializeConfiguration("jms.incoming.osgp.core.requests", this.incomingOsgpCoreRequestMessageListener)
                .getMessageListenerContainer();
    }

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp
    // core)

    @Bean(name = "domainSmartMeteringOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.outgoing.osgp.core.responses").getJmsTemplate();
    }
}
