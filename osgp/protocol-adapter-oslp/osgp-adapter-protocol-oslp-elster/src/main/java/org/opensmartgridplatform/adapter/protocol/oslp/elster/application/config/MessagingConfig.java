/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundSigningServerResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundSigningServerRequestsMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-oslp-elster.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolOslpElster/config}", ignoreResourceNotFound = true)
@Import(value = { InboundOsgpCoreRequestsMessagingConfig.class, InboundOsgpCoreResponsesMessagingConfig.class,
        InboundSigningServerResponsesMessagingConfig.class, OutboundLogItemRequestsMessagingConfig.class,
        OutboundOsgpCoreRequestsMessagingConfig.class, OutboundOsgpCoreResponsesMessagingConfig.class,
        OutboundSigningServerRequestsMessagingConfig.class })
public class MessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        LOGGER.info("Initializing defaultJmsConfiguration bean.");
        return new DefaultJmsConfiguration();
    }
}
