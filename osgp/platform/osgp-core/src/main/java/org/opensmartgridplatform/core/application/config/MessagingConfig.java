// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
