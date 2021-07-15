/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolRequestService;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolResponseService;
import org.opensmartgridplatform.core.infra.jms.domain.outbound.DomainRequestMessageSender;
import org.opensmartgridplatform.core.infra.jms.domain.outbound.DomainResponseMessageSender;
import org.opensmartgridplatform.core.infra.jms.protocol.outbound.ProtocolRequestMessageSender;
import org.opensmartgridplatform.core.infra.jms.protocol.outbound.ProtocolResponseMessageSender;
import org.opensmartgridplatform.core.infra.messaging.CoreLogItemRequestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java configuration requires Spring
 * Framework 3.0
 */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.shared.domain.services",
      "org.opensmartgridplatform.domain.core",
      "org.opensmartgridplatform.core"
    })
@EnableTransactionManagement()
@Import({MessagingConfig.class})
public class ApplicationContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

  public ApplicationContext() {
    // empty constructor
  }

  @Bean
  ProtocolResponseService protocolResponseMessageSender() {
    LOGGER.debug("Creating bean: protocolResponseMessageSender");
    return new ProtocolResponseMessageSender();
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
  DomainRequestService domainRequestMessageSender() {
    LOGGER.debug("Creating bean: domainRequestMessageSender");
    return new DomainRequestMessageSender();
  }

  @Bean
  public CoreLogItemRequestMessageSender coreLogItemRequestMessageSender() {
    return new CoreLogItemRequestMessageSender();
  }
}
