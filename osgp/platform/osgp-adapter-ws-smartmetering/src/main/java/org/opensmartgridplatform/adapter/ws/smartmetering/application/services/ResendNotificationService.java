// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "resendNotificationServiceSmartmetering")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService<NotificationType> {

  private final NotificationService smartMeteringNotificationService;
  private final String webserviceNotificationApplicationName;

  public ResendNotificationService(
      final NotificationService smartMeteringNotificationService,
      final String webserviceNotificationApplicationName) {
    super(NotificationType.class);
    this.smartMeteringNotificationService = smartMeteringNotificationService;
    this.webserviceNotificationApplicationName = webserviceNotificationApplicationName;
  }

  @PostConstruct
  public void initialize() {
    this.setNotificationService(this.smartMeteringNotificationService);
    this.setApplicationName(this.webserviceNotificationApplicationName);
  }
}
