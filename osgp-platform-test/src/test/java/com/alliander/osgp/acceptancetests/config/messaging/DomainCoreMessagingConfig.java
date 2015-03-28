package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.core.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;

public class DomainCoreMessagingConfig {

    // === JMS SETTINGS: COMMON WEB SERVICE REQUESTS ===

    @Bean(name = "domainCoreIncomingWebServiceRequestsQueue")
    public ActiveMQDestination commonWsRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_CORE_1_0__WS_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainCoreIncomingRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer commonWsRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.commonWsRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.webServiceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainCoreIncomingWebServiceRequestsMessageListener")
    private WebServiceRequestMessageListener webServiceRequestMessageListener;

    // === JMS SETTINGS: COMMON WEB SERVICE RESPONSES ===

    @Bean(name = "domainCoreOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate commonWsResponsesJmsTemplate() {
        //        return mock(JmsTemplate.class);
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonWsResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination commonWsResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_CORE_1_0__DOMAIN_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesMessageSender")
    public WebServiceResponseMessageSender commonWsResponsesMessageSender() {
        return mock(WebServiceResponseMessageSender.class);
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE REQUESTS ===

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate osgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpCoreRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination osgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_CORE_1_0__REQUESTS_QUEUE);
    }

    //    @Bean
    //    public OsgpCoreRequestMessageSender osgpCoreRequestMessageSender() {
    //        return new OsgpCoreRequestMessageSender();
    //    }

    // === JMS SETTINGS: OSGP DOMAIN CORE RESPONSES ===

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination osgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_CORE_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpCoreResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.osgpCoreResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreResponsesMessageListener")
    public OsgpCoreResponseMessageListener osgpCoreResponseMessageListener;

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING REQUESTS ===

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination osgpCoreRequestsIncomingQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_CORE_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreRequestsIncomingMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpCoreRequestsIncomingQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.osgpCoreRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreRequestsMessageListener")
    public OsgpCoreRequestMessageListener osgpCoreRequestMessageListener;

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING RESPONSES ===

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate osgpCoreResponsesIncomingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpCoreResponsesIncomingQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination osgpCoreResponsesIncomingQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_CORE_1_0__RESPONSES_QUEUE);
    }

    //    @Bean
    //    public OsgpCoreResponseMessageSender osgpCoreResponseMessageSender() {
    //        return new OsgpCoreResponseMessageSender();
    //    }
}
