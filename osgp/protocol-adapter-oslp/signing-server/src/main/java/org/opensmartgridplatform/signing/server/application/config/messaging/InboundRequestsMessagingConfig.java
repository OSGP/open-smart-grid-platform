/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class for inbound requests.
 */
@Configuration
@EnableTransactionManagement()
public class InboundRequestsMessagingConfig {

    private JmsConfigurationFactory jmsConfigurationFactory;

    public InboundRequestsMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_SIGNING_SERVER_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "signingServerInboundRequestsConnectionFactory")
    public ConnectionFactory connectionFactory() {
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "signingServerInboundRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("signingServerRequestsMessageListener") final MessageListener messageListener) {
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }
}
