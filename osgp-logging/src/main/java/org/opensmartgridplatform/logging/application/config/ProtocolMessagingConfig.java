/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.application.config;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.logging.infra.jms.ProtocolLogItemRequestMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-logging.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/Logging/config}", ignoreResourceNotFound = true), })
public class ProtocolMessagingConfig extends AbstractMessagingConfig {

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.protocol.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.protocol.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.protocol.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.protocol.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.protocol.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.protocol.default.use.exponential.back.off";

    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "max.retry.count";

    @Override
    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === INCOMING PROTOCOL LOG ITEM REQUESTS ===
    // beans used for receiving protocol log item request messages

    @Bean
    public JmsConfiguration protocolLogItemRequestsJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration(
                JmsConfigurationNames.JMS_PROTOCOL_LOG_ITEM_REQUESTS, this.protocolLogItemRequestMessageListener());
    }

    @Bean
    public DefaultMessageListenerContainer protocolLogItemRequestsMessageListenerContainer(
            final JmsConfiguration protocolLogItemRequestsJmsConfiguration) {
        return protocolLogItemRequestsJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    public ProtocolLogItemRequestMessageListener protocolLogItemRequestMessageListener() {
        return new ProtocolLogItemRequestMessageListener();
    }

    // The Max count to retry a failed response

    @Bean
    public int getMaxRetryCount() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }
}
