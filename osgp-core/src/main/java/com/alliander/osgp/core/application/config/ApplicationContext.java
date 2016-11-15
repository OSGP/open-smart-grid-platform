/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolRequestService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolResponseService;
import com.alliander.osgp.core.infra.jms.domain.DomainResponseMessageSender;
import com.alliander.osgp.core.infra.jms.domain.in.DomainRequestMessageSender;
import com.alliander.osgp.core.infra.jms.protocol.ProtocolRequestMessageSender;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolResponseMessageSender;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.core" })
@EnableTransactionManagement()
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    public ApplicationContext() {
        // empty constructor
    }

    @Bean
    ProtocolRequestService protocolRequestMessageSender() {
        LOGGER.debug("Creating bean: protocolRequestMessageSender");
        return new ProtocolRequestMessageSender();
    }

    @Bean
    DomainResponseService domainResponseMessageSender() {
        LOGGER.debug("Creating bean: domainResponseMessageSender");
        return new DomainResponseMessageSender();
    }

    @Bean
    ProtocolResponseService protocolResponseMessageSender() {
        LOGGER.debug("Creating bean: protocolResponseMessageSender");
        return new ProtocolResponseMessageSender();
    }

    @Bean
    DomainRequestService domainRequestMessageSender() {
        LOGGER.debug("Creating bean: domainRequestMessageSender");
        return new DomainRequestMessageSender();
    }

}
