/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.config;

import org.opensmartgridplatform.adapter.ws.core.application.services.CoreNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationClientConfigBase;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsCore/config}", ignoreResourceNotFound = true)
public class CoreNotificationClientConfig extends NotificationClientConfigBase {

  @Bean
  public NotificationService notificationService() {
    if (this.isWebserviceNotificationEnabled()) {
      return new CoreNotificationService();
    } else {
      return new NotificationServiceBlackHole();
    }
  }
}
