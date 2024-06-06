// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.InboundWebServiceRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging.OutboundWebServiceResponsesMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/** An application context Java configuration class. */
@Configuration
@PropertySource("classpath:osgp-adapter-domain-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterDomainPublicLighting/config}",
    ignoreResourceNotFound = true)
@Import(
    value = {
      InboundOsgpCoreRequestsMessagingConfig.class,
      InboundOsgpCoreResponsesMessagingConfig.class,
      InboundWebServiceRequestsMessagingConfig.class,
      OutboundOsgpCoreRequestsMessagingConfig.class,
      OutboundOsgpCoreResponsesMessagingConfig.class,
      OutboundWebServiceResponsesMessagingConfig.class
    })
public class MessagingConfig extends AbstractConfig {

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }
}
