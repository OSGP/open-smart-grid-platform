package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.admin.infra.jms.AdminRequestMessageSender;
import com.alliander.osgp.adapter.ws.admin.infra.jms.AdminResponseMessageFinder;

//@Configuration
public class WsAdminMessagingConfig {

    // === JMS SETTINGS: ADMIN REQUESTS ===

    @Bean(name = "wsAdminOutgoingRequestsJmsTemplate")
    public JmsTemplate adminRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.adminRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "wsAdminOutgoingRequestsQueue")
    public ActiveMQDestination adminRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_ADMIN_1_0__WS_ADMIN_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "wsAdminOutgoingRequestMessageSender")
    public AdminRequestMessageSender adminRequestMessageSender() {
        return new AdminRequestMessageSender();
    }

    // === JMS SETTINGS: ADMIN RESPONSES ===

    @Bean(name = "wsAdminIncomingResponsesJmsTemplate")
    public JmsTemplate adminResponsesJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean(name = "wsAdminIncomingResponsesQueue")
    public ActiveMQDestination adminResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_ADMIN_1_0__DOMAIN_ADMIN_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "wsAdminIncomingResponsesMessageFinder")
    public AdminResponseMessageFinder adminResponseMessageFinder() {
        return new AdminResponseMessageFinder();
    }

}
