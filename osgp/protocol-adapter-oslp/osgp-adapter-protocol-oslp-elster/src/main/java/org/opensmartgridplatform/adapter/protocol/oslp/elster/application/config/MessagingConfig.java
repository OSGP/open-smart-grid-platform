// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.InboundSigningServerResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundOsgpCoreRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging.OutboundSigningServerRequestsMessagingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** An application context Java configuration class. */
@Configuration
@Import(
    value = {
      InboundOsgpCoreRequestsMessagingConfig.class,
      InboundOsgpCoreResponsesMessagingConfig.class,
      InboundSigningServerResponsesMessagingConfig.class,
      OutboundLogItemRequestsMessagingConfig.class,
      OutboundOsgpCoreRequestsMessagingConfig.class,
      OutboundOsgpCoreResponsesMessagingConfig.class,
      OutboundSigningServerRequestsMessagingConfig.class
    })
public class MessagingConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    LOGGER.info("Initializing defaultJmsConfiguration bean.");
    return new DefaultJmsConfiguration();
  }
}
