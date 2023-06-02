//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "resendNotificationServiceSmartmetering")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService<NotificationType> {

  @Autowired private NotificationService smartMeteringNotificationService;

  @Autowired private String webserviceNotificationApplicationName;

  public ResendNotificationService() {
    super(NotificationType.class);
  }

  @PostConstruct
  public void initialize() {
    this.setNotificationService(this.smartMeteringNotificationService);
    this.setApplicationName(this.webserviceNotificationApplicationName);
  }
}
