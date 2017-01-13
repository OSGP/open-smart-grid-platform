package com.alliander.osgp.shared.application.config.jms;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class JmsConfiguration {
    private JmsTemplate jmsTemplate;

    private RedeliveryPolicy redeliveryPolicy;

    private DefaultMessageListenerContainer messageListenerContainer;

    public JmsTemplate getJmsTemplate() {
        return this.jmsTemplate;
    }

    public void setJmsTemplate(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public RedeliveryPolicy getRedeliveryPolicy() {
        return this.redeliveryPolicy;
    }

    public void setRedeliveryPolicy(final RedeliveryPolicy redeliveryPolicy) {
        this.redeliveryPolicy = redeliveryPolicy;
    }

    public DefaultMessageListenerContainer getMessageListenerContainer() {
        return this.messageListenerContainer;
    }

    public void setMessageListenerContainer(final DefaultMessageListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }

}
