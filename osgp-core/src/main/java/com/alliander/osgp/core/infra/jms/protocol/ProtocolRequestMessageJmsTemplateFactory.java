package com.alliander.osgp.core.infra.jms.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.core.infra.jms.JmsTemplateSettings;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;

public class ProtocolRequestMessageJmsTemplateFactory implements InitializingBean {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplateSettings jmsTemplateSettings;

    private Map<String, JmsTemplate> jmsTemplateMap = new HashMap<>();

    public ProtocolRequestMessageJmsTemplateFactory(final ConnectionFactory connectionFactory,
            final JmsTemplateSettings jmsTemplateSettings, final List<ProtocolInfo> protocolInfos) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplateSettings = jmsTemplateSettings;

        for (final ProtocolInfo protocolInfo : protocolInfos) {
            this.jmsTemplateMap.put(protocolInfo.getKey(), this.createJmsTemplate(protocolInfo));
        }
    }

    public JmsTemplate getJmsTemplate(final ProtocolInfo protocolInfo) {
        return this.jmsTemplateMap.get(protocolInfo.getKey());
    }

    public JmsTemplate getJmsTemplate(final String protocol, final String protocolVersion) {
        return this.jmsTemplateMap.get(ProtocolInfo.getKey(protocol, protocolVersion));
    }

    @Override
    public void afterPropertiesSet() {
        for (final JmsTemplate jmsTemplate : this.jmsTemplateMap.values()) {
            jmsTemplate.afterPropertiesSet();
        }
    }

    private JmsTemplate createJmsTemplate(final ProtocolInfo protocolInfo) {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(protocolInfo.getOutgoingProtocolRequestsQueue()));
        jmsTemplate.setConnectionFactory(this.connectionFactory);
        jmsTemplate.setExplicitQosEnabled(this.jmsTemplateSettings.isExplicitQosEnabled());
        jmsTemplate.setTimeToLive(this.jmsTemplateSettings.getTimeToLive());
        jmsTemplate.setDeliveryPersistent(this.jmsTemplateSettings.isDeliveryPersistent());
        return jmsTemplate;
    }
}
