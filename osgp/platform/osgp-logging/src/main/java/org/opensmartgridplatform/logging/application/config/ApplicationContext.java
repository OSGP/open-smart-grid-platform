//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.logging.application.config;

import org.opensmartgridplatform.logging.application.config.messaging.InboundLoggingRequestsMessagingConfig;
import org.opensmartgridplatform.logging.application.config.messaging.InboundProtocolLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.logging.infra.jms.LoggingMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/** An application context Java configuration class. */
@Configuration
@ComponentScan(
    basePackageClasses = {
      PersistenceConfig.class,
      LoggingMessageListener.class,
      InboundLoggingRequestsMessagingConfig.class
    })
@PropertySource("classpath:osgp-logging.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Logging/config}", ignoreResourceNotFound = true)
@Import({
  InboundLoggingRequestsMessagingConfig.class,
  InboundProtocolLogItemRequestsMessagingConfig.class,
  MetricsConfig.class
})
public class ApplicationContext extends AbstractConfig {

  @Value(value = "${max.retry.count:3}")
  private int maxRetryCount;

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }

  // The Max count to retry a failed response
  @Bean
  public int getMaxRetryCount() {
    return this.maxRetryCount;
  }
}
