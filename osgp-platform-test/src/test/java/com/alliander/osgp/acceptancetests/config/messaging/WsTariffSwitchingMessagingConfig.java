package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;

//@Configuration
public class WsTariffSwitchingMessagingConfig {

    // === JMS SETTINGS: TARIFF SWITCHING REQUESTS ===

    @Bean(name = "wsTariffSwitchingOutgoingRequestsQueue")
    public ActiveMQDestination tariffSwitchingRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_TARIFFSWITCHING_1_0__WS_TARIFFSWITCHING_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "wsTariffSwitchingOutgoingRequestsJmsTemplate")
    public JmsTemplate tariffSwitchingRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.tariffSwitchingRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "wsTariffSwitchingOutgoingRequestsMessageSender")
    public TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender() {
        return new TariffSwitchingRequestMessageSender();
    }

    // === JMS SETTINGS: TARIFF SWITCHING RESPONSES ===

    @Bean(name = "wsTariffSwitchingIncomingResponsesQueue")
    public ActiveMQDestination tariffSwitchingResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_TARIFFSWITCHING_1_0__DOMAIN_TARIFFSWITCHING_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "wsTariffSwitchingIncomingResponsesJmsTemplate")
    public JmsTemplate tariffSwitchingResponsesJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean(name = "wsTariffSwitchingIncomingResponsesMessageFinder")
    public TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder() {
        return new TariffSwitchingResponseMessageFinder();
    }
}
