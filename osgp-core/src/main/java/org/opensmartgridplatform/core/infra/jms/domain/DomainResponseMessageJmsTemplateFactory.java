/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

import org.opensmartgridplatform.core.infra.jms.JmsTemplateSettings;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.shared.infra.jms.OsgpJmsTemplate;

public class DomainResponseMessageJmsTemplateFactory implements InitializingBean {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplateSettings jmsTemplateSettings;

    private final Map<String, JmsTemplate> jmsTemplateMap = new HashMap<>();

    public DomainResponseMessageJmsTemplateFactory(final ConnectionFactory connectionFactory,
            final JmsTemplateSettings jmsTemplateSettings, final List<DomainInfo> domainInfos) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplateSettings = jmsTemplateSettings;

        for (final DomainInfo domainInfo : domainInfos) {
            this.jmsTemplateMap.put(domainInfo.getKey(), this.createJmsTemplate(domainInfo));
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
        final OsgpJmsTemplate jmsTemplate = new OsgpJmsTemplate();
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(domainInfo.getOutgoingDomainResponsesQueue()));
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(this.jmsTemplateSettings.isExplicitQosEnabled());
        jmsTemplate.setTimeToLive(this.jmsTemplateSettings.getTimeToLive());
        jmsTemplate.setDeliveryPersistent(this.jmsTemplateSettings.isDeliveryPersistent());
        jmsTemplate.setConnectionFactory(this.connectionFactory);
        return jmsTemplate;
    }
}
