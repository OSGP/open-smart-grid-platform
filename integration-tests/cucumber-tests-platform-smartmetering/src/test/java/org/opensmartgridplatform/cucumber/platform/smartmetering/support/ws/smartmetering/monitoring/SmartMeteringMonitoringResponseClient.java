/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringMonitoringResponseClient<T, V extends AsyncRequest>
    extends SmartMeteringBaseClient {

  @Autowired
  private DefaultWebServiceTemplateFactory smartMeteringMonitoringWebServiceTemplateFactory;

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringMonitoringWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }

  @SuppressWarnings("unchecked")
  public T getResponse(final V request) throws WebServiceSecurityException {

    this.waitForNotification(request.getCorrelationUid());
    return (T) this.getTemplate().marshalSendAndReceive(request);
  }

  public T getResponse(final V request, final NotificationType notificationType)
      throws WebServiceSecurityException {
    final Notification notification = this.waitForNotification(notificationType);
    request.setCorrelationUid(notification.getCorrelationUid());
    return (T) this.getTemplate().marshalSendAndReceive(request);
  }

  public boolean hasMoreResponses() throws WebServiceSecurityException {
    final Notification notification = this.waitForNotification(5000);
    return notification != null;
  }

  public boolean hasMoreResponses(final NotificationType notificationType)
      throws WebServiceSecurityException {
    final Notification notification = this.waitForNotification(notificationType, 5000);
    return notification != null;
  }
}
