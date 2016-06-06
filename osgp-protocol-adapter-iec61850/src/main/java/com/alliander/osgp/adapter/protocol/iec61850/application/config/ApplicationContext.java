/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.iec61850.device.FirmwareLocation;
import com.alliander.osgp.core.db.api.iec61850.application.config.Iec61850OsgpCoreDbApiPersistenceConfig;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.adapter.protocol.iec61850", "com.alliander.osgp.core.db.api" })
@EnableTransactionManagement()
@Import({ MessagingConfig.class, Iec61850OsgpCoreDbApiPersistenceConfig.class, Iec61850Config.class })
@PropertySource("file:${osp/osgpAdapterProtocolIec61850/config}")
public class ApplicationContext {

    @Value("${retrycount.max}")
    private int maxRetryCount;

    @Value("${selftest.timeout}")
    private int selftestTimeout;

    @Value("${connection.reponse.timeout}")
    private int responseTimeout;

    /**
     * The number of times the communication with the device is retried
     */
    @Bean
    public int maxRetryCount() {
        return this.maxRetryCount;
    }

    /**
     * The amount of time, in milliseconds, the library will wait for a
     * response.
     */
    @Bean
    public int responseTimeout() {
        return this.responseTimeout;
    }

    /**
     * The amount of time, in milliseconds, between the switching of the relays
     * and the status check in the selftest
     */
    @Bean
    public int selftestTimeout() {
        return this.selftestTimeout;
    }

    @Bean
    public FirmwareLocation firmwareLocation(@Value("${firmware.protocol}") final String protocol,
            @Value("${firmware.domain}") final String domain, @Value("${firmware.port}") final int port,
            @Value("${firmware.path}") final String path, @Value("${firmware.fileExtension}") final String fileExtension) {
        return new FirmwareLocation(protocol, domain, port, path, fileExtension);
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySource = new PropertySourcesPlaceholderConfigurer();
        propertySource.setIgnoreUnresolvablePlaceholders(true);
        return propertySource;
    }
}
