// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.application.services;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "resendNotificationServiceMicrogrids")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService<NotificationType> {

  @Autowired private NotificationService microgridsNotificationService;

  @Value("${web.service.notification.application.name}")
  private String webserviceNotificationApplicationName;

  public ResendNotificationService() {
    super(NotificationType.class);
  }

  @PostConstruct
  public void initialize() {
    this.setNotificationService(this.microgridsNotificationService);
    this.setApplicationName(this.webserviceNotificationApplicationName);
  }
}
