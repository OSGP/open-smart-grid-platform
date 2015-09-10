/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.config;

import javax.annotation.Resource;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.oslp.device.FirmwareLocation;
import com.alliander.osgp.core.db.api.application.config.OsgpCoreDbApiPersistenceConfig;
import com.alliander.osgp.shared.application.config.PagingSettings;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.adapter.protocol.oslp", "com.alliander.osgp.core.db.api" })
@EnableTransactionManagement()
@Import({ MessagingConfig.class, OslpConfig.class, OslpPersistenceConfig.class, OsgpCoreDbApiPersistenceConfig.class })
@PropertySource("file:${osp/osgpAdapterProtocolOslp/config}")
public class ApplicationContext {

    private static final String PROPERTY_NAME_FIRMWARE_DOMAIN = "firmware.domain";
    private static final String PROPERTY_NAME_FIRMWARE_PATH = "firmware.path";
    private static final String PROPERTY_NAME_FIRMWARE_FILE_EXTENSION = "firmware.fileExtension";
    private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
    private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

    private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
    private static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
    private static final int TIME_ZONE_OFFSET_MINUTES = LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis())
            / DateTimeConstants.MILLIS_PER_MINUTE;

    @Resource
    private Environment environment;

    public ApplicationContext() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    /**
     * @return
     */
    @Bean(name = "oslpPagingSettings")
    public PagingSettings pagingSettings() {
        return new PagingSettings(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)), Integer.parseInt(this.environment
                        .getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));
    }

    /**
     * @return
     */
    @Bean
    public FirmwareLocation firmwareLocation() {
        return new FirmwareLocation(this.environment.getProperty(PROPERTY_NAME_FIRMWARE_DOMAIN),
                this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PATH),
                this.environment.getProperty(PROPERTY_NAME_FIRMWARE_FILE_EXTENSION));
    }

    // === Time zone config ===

    @Bean
    public String localTimeZoneIdentifier() {
        return LOCAL_TIME_ZONE_IDENTIFIER;
    }

    @Bean
    public DateTimeZone localTimeZone() {
        return LOCAL_TIME_ZONE;
    }

    @Bean
    public Integer timeZoneOffsetMinutes() {
        return TIME_ZONE_OFFSET_MINUTES;
    }
}
