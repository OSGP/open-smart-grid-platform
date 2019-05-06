/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@ComponentScan(basePackages = { "org.opensmartgridplatform.adapter.protocol.iec60870",
        "org.opensmartgridplatform.shared.domain.services" })
@EnableTransactionManagement()
@Import({ Iec60870MessagingConfig.class, Iec60870Config.class })
@PropertySource("classpath:osgp-adapter-protocol-iec60870.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolIec60870/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "retrycount.max";

    @Value("${close.connections.on.broker.failure:false}")
    private boolean closeConnectionsOnBrokerFailure;

    @Bean
    public boolean isCloseConnectionsOnBrokerFailure() {
        return this.closeConnectionsOnBrokerFailure;
    }

    /**
     * The number of times the communication with the device is retried
     */
    @Bean
    public int maxRetryCount() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }

}
