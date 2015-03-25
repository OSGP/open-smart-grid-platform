package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.WebServiceResponseMessageSender;

public class DomainTariffSwitchingMessagingConfig {
    // JMS SETTINGS: INCOMING WEB SERVICE REQUESTS ===

    @Bean(name = "domainTariffSwitchingIncomingWebServiceRequestsQueue")
    public ActiveMQDestination incomingWebServiceRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_TARIFFSWITCHING_1_0__WS_TARIFFSWITCHING_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainTariffSwitchingIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingWebServiceRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingWebServiceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainTariffSwitchingIncomingWebServiceRequestMessageListener")
    public WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES

    @Bean(name = "domainTariffSwitchingOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingWebServiceResponsesQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean(name = "domainTariffSwitchingOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination outgoingWebServiceResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_TARIFFSWITCHING_1_0__DOMAIN_TARIFFSWITCHING_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainTariffSwitchingOutgoingWebServiceResponseMessageSender")
    public WebServiceResponseMessageSender outgoingWebServiceResponseMessageSenderMock() {
        return mock(WebServiceResponseMessageSender.class);
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)

    @Bean(name = "domainTariffSwitchingOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainTariffSwitchingOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination outgoingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_TARIFFSWITCHING_1_0__REQUESTS_QUEUE);
    }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp core)

    @Bean(name = "domainTariffSwitchingIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination incomingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_TARIFFSWITCHING_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainTariffSwitchingIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainTariffSwitchingIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp core) 

    @Bean(name = "domainTariffSwitchingIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination incomingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_TARIFFSWITCHING_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainTariffSwitchingIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainTariffSwitchingIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp core)

    @Bean(name = "domainTariffSwitchingOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreResponsesQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainTariffSwitchingOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination outgoingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_TARIFFSWITCHING_1_0__RESPONSES_QUEUE);
    }
}
