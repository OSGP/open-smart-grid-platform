//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.admin.application.config;

import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.InboundDomainResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.OutboundDomainRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.ws.admin.application.config.messaging.OutboundLoggingRequestsMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
@Import({
  OutboundDomainRequestsMessagingConfig.class,
  InboundDomainResponsesMessagingConfig.class,
  OutboundLoggingRequestsMessagingConfig.class
})
public class MessagingConfig extends AbstractConfig {

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }
}
