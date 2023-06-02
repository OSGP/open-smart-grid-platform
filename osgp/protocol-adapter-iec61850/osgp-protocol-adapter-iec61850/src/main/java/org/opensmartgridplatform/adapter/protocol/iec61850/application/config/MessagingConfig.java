//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging.InboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging.InboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging.OutboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
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
@PropertySource("classpath:osgp-adapter-protocol-iec61850.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterProtocolIec61850/config}",
    ignoreResourceNotFound = true)
@Import(
    value = {
      InboundOsgpCoreRequestsMessagingConfig.class,
      InboundOsgpCoreResponsesMessagingConfig.class,
      OutboundLogItemRequestsMessagingConfig.class,
      OutboundOsgpCoreRequestsMessagingConfig.class,
      OutboundOsgpCoreResponsesMessagingConfig.class
    })
public class MessagingConfig extends AbstractConfig {

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }
}
