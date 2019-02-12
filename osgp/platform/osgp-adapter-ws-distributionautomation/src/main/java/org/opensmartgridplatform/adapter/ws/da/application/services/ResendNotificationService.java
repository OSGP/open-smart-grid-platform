/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.services;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "resendNotificationServiceDistributionAutomation")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService<NotificationType> {

    @Autowired
    private NotificationService distributionAutomationNotificationService;

    @Value("${web.service.notification.application.name}")
    private String webserviceNotificationApplicationName;

    public ResendNotificationService() {
        super(NotificationType.class);
    }

    @PostConstruct
    public void initialize() {
        this.setNotificationService(this.distributionAutomationNotificationService);
        this.setApplicationName(this.webserviceNotificationApplicationName);
    }
}
