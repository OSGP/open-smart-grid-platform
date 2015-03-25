package com.alliander.osgp.core.infra.jms.domain.in;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.core.infra.jms.JmsTemplateSettings;
import com.alliander.osgp.domain.core.entities.DomainInfo;

public class DomainRequestMessageJmsTemplateFactory implements InitializingBean {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplateSettings jmsTemplateSettings;

    private Map<String, JmsTemplate> jmsTemplateMap = new HashMap<>();

    public DomainRequestMessageJmsTemplateFactory(final ConnectionFactory connectionFactory,
            final JmsTemplateSettings jmsTemplateSettings, final List<DomainInfo> domainInfos) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplateSettings = jmsTemplateSettings;

        for (final DomainInfo domainInfo : domainInfos) {
            // Check if the queue name is present.
            if (domainInfo.getOutgoingDomainRequestsQueue() != null) {
                this.jmsTemplateMap.put(domainInfo.getKey(), this.createJmsTemplate(domainInfo));
            }
        }
    }

    public JmsTemplate getJmsTemplate(final String key) {
        return this.jmsTemplateMap.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (final JmsTemplate jmsTemplate : this.jmsTemplateMap.values()) {
            jmsTemplate.afterPropertiesSet();
        }
    }

    private JmsTemplate createJmsTemplate(final DomainInfo domainInfo) {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(domainInfo.getOutgoingDomainRequestsQueue()));
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(this.jmsTemplateSettings.isExplicitQosEnabled());
        jmsTemplate.setTimeToLive(this.jmsTemplateSettings.getTimeToLive());
        jmsTemplate.setDeliveryPersistent(this.jmsTemplateSettings.isDeliveryPersistent());
        jmsTemplate.setConnectionFactory(this.connectionFactory);
        return jmsTemplate;
    }
}
