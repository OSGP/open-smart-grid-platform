/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericSendNotificationRequest;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.WebserviceTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

public abstract class AbstractNotificationServiceWs {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationServiceWs.class);

  protected final String notificationUsername;
  protected final String notificationUrl;
  protected final String notificationOrganisation;

  protected AbstractNotificationServiceWs(
      final String notificationUrl,
      final String notificationUsername,
      final String notificationOrganisation) {
    this.notificationUrl = notificationUrl;
    this.notificationUsername = notificationUsername;
    this.notificationOrganisation = notificationOrganisation;
  }

  protected void doSendNotification(
      final WebserviceTemplateFactory wsTemplateFactory,
      final String organisationIdentification,
      final String userName,
      final String notificationURL,
      final Object notification) {

    try {
      /*
       * Get a template for the organisation representing the OSGP
       * platform, on behalf of which the notification is sent to the
       * organisation identified by the organisationIdentification.
       */
      final WebServiceTemplate wsTemplate =
          wsTemplateFactory.getTemplate(this.notificationOrganisation, userName, notificationURL);
      wsTemplate.marshalSendAndReceive(notification);
    } catch (WebServiceSecurityException | WebServiceIOException | SoapFaultClientException e) {
      final String msg =
          String.format(
              "error sending notification message org=%s, user=%s, to org=%s, notifyUrl=%s, errmsg=%s",
              this.notificationOrganisation,
              userName,
              organisationIdentification,
              notificationURL,
              e.getMessage());
      LOGGER.error(msg, e);
    }
  }

  protected String retrieveNotificationUrl(
      final ResponseUrlService responseUrlService, final String correlationUid) {
    final String responseUrl = responseUrlService.findResponseUrl(correlationUid);
    return (responseUrl == null) ? this.notificationUrl : responseUrl;
  }

  protected GenericSendNotificationRequest genericNotificationRequest(
      final String deviceIdentification,
      final String result,
      final String correlationUid,
      final String message,
      final String notificationType) {

    final GenericNotification notification =
        new GenericNotification(
            message, result, deviceIdentification, correlationUid, notificationType);
    return new GenericSendNotificationRequest(notification);
  }
}
