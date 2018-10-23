/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

import org.opensmartgridplatform.core.infra.jms.JmsTemplateSettings;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.OsgpJmsTemplate;

public class ProtocolRequestMessageJmsTemplateFactory implements InitializingBean {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplateSettings jmsTemplateSettings;

    private final Map<String, JmsTemplate> jmsTemplateMap = new HashMap<>();

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
        final OsgpJmsTemplate jmsTemplate = new OsgpJmsTemplate();
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(protocolInfo.getOutgoingProtocolRequestsQueue()));
        jmsTemplate.setConnectionFactory(this.connectionFactory);
        jmsTemplate.setExplicitQosEnabled(this.jmsTemplateSettings.isExplicitQosEnabled());
        jmsTemplate.setTimeToLive(this.jmsTemplateSettings.getTimeToLive());
        jmsTemplate.setDeliveryPersistent(this.jmsTemplateSettings.isDeliveryPersistent());
        return jmsTemplate;
    }
}
