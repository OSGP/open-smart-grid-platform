/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.application.config;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:signing-server.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SigningServer/config}", ignoreResourceNotFound = true)
public class IncomingRequestsMessagingConfig extends AbstractMessagingConfig {

    private JmsConfigurationFactory incomingRequestsJmsConfigurationFactory;

    public IncomingRequestsMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.incomingRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_SIGNING_SERVER_REQUESTS);
    }

    @Bean(destroyMethod = "stop")
    public ConnectionFactory incomingSigningServerRequestsConnectionFactory() {
        return this.incomingRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean("signingServerRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingSigningServerRequestsMessageListenerContainer(
            @Qualifier("signingServerRequestsMessageListener") final MessageListener requestsMessageListener) {
        return this.incomingRequestsJmsConfigurationFactory.initMessageListenerContainer(requestsMessageListener);
    }
}
