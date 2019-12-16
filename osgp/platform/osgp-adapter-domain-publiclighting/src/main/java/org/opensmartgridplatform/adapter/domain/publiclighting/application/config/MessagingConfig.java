/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundWebServiceRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundWebServiceResponsesMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * An application context Java configuration class.
 */
@Configuration
@PropertySource("classpath:osgp-adapter-domain-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainPublicLighting/config}", ignoreResourceNotFound = true)
@Import(value = { InboundOsgpCoreRequestsMessagingConfig.class, InboundOsgpCoreResponsesMessagingConfig.class,
        InboundWebServiceRequestsMessagingConfig.class, OutboundOsgpCoreRequestsMessagingConfig.class,
        OutboundOsgpCoreResponsesMessagingConfig.class, OutboundWebServiceResponsesMessagingConfig.class })
public class MessagingConfig extends AbstractConfig {

    private static final String PROPERTY_NAME_JMS_GET_POWER_USAGE_HISTORY_RESPONSE_TIME_TO_LIVE = "jms.get.power.usage.history.response.time.to.live";

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        return new DefaultJmsConfiguration();
    }

    @Bean
    public Long getPowerUsageHistoryResponseTimeToLive() {
        return Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_GET_POWER_USAGE_HISTORY_RESPONSE_TIME_TO_LIVE));
    }
}
