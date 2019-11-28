/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.config;

import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.InboundDomainResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.OutboundDomainRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.OutboundLoggingRequestsMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
@Import({ OutboundDomainRequestsMessagingConfig.class, InboundDomainResponsesMessagingConfig.class,
        OutboundLoggingRequestsMessagingConfig.class })
public class MessagingConfig extends AbstractConfig {

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        return new DefaultJmsConfiguration();
    }
}
