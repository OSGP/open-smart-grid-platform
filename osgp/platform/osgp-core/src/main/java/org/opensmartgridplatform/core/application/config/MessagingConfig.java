/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import org.opensmartgridplatform.core.application.config.messaging.DomainMessagingConfig;
import org.opensmartgridplatform.core.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.core.application.config.messaging.ProtocolMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** An application context Java configuration class. */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
@Import(
    value = {
      DomainMessagingConfig.class,
      ProtocolMessagingConfig.class,
      OutboundLogItemRequestsMessagingConfig.class
    })
public class MessagingConfig extends AbstractConfig {

  /**
   * Creates a bean for default JMS configuration settings
   *
   * @return the DefaultJmsConfiguration bean
   */
  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }
}
