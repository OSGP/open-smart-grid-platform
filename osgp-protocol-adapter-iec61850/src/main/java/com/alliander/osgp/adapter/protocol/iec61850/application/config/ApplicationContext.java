/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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

    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "retrycount.max";
    private static final String PROPERTY_NAME_SELFTEST_TIMEOUT = "selftest.timeout";

    private static final String PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT = "connection.reponse.timeout";

    private static final String PROPERTY_NAME_FIRMWARE_PROTOCOL = "firmware.protocol";
    private static final String PROPERTY_NAME_FIRMWARE_DOMAIN = "firmware.domain";
    private static final String PROPERTY_NAME_FIRMWARE_PORT = "firmware.port";
    private static final String PROPERTY_NAME_FIRMWARE_PATH = "firmware.path";

    @Resource
    private Environment environment;

    /**
     * The number of times the communication with the device is retried
     */
    @Bean
    public int maxRetryCount() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }

    /**
     * The amount of time, in milliseconds, the library will wait for a
     * response.
     */
    @Bean
    public int responseTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT));
    }

    /**
     * The amount of time, in milliseconds, between the switching of the relays
     * and the status check in the selftest
     */
    @Bean
    public int selftestTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_SELFTEST_TIMEOUT));
    }

    @Bean
    public FirmwareLocation firmwareLocation() {
        return new FirmwareLocation(this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PROTOCOL),
                this.environment.getProperty(PROPERTY_NAME_FIRMWARE_DOMAIN), Integer.parseInt(this.environment
                        .getProperty(PROPERTY_NAME_FIRMWARE_PORT)),
                        this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PATH));
    }
}
