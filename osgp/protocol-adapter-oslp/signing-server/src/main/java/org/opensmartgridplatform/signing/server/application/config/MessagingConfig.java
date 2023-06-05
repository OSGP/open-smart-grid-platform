// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.signing.server.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.signing.server.application.config.messaging.InboundRequestsMessagingConfig;
import org.opensmartgridplatform.signing.server.application.config.messaging.OutboundResponsesMessagingConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** An application context Java configuration class. */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:signing-server.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SigningServer/config}", ignoreResourceNotFound = true)
@Import(value = {InboundRequestsMessagingConfig.class, OutboundResponsesMessagingConfig.class})
public class MessagingConfig extends AbstractConfig {

  @Bean
  public JmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }
}
