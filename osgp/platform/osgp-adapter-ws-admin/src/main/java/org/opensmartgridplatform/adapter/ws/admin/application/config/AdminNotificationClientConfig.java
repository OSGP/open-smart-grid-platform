// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.application.config;

import org.opensmartgridplatform.adapter.ws.admin.application.services.AdminNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationClientConfigBase;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class AdminNotificationClientConfig extends NotificationClientConfigBase {

  @Bean
  public NotificationService notificationService() {
    if (this.isWebserviceNotificationEnabled()) {
      return new AdminNotificationService();
    } else {
      return new NotificationServiceBlackHole();
    }
  }
}
