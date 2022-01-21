/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DomainResponseMessageProcessor implements MessageProcessor {

  private final NotificationService notificationService;
  private final ResponseDataService responseDataService;
  private final String webserviceNotificationApplicationName;

  public DomainResponseMessageProcessor(
      final NotificationService notificationService,
      final ResponseDataService responseDataService,
      final String webserviceNotificationApplicationName) {
    this.notificationService = notificationService;
    this.responseDataService = responseDataService;
    this.webserviceNotificationApplicationName = webserviceNotificationApplicationName;
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    log.debug("Processing response message");

    String correlationUid = null;
    String actualMessageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;

    final String notificationMessage;
    final NotificationType notificationType;
    final ResponseMessageResultType resultType;
    final String resultDescription;
    final Serializable dataObject;

    try {
      correlationUid = message.getJMSCorrelationID();
      actualMessageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
      resultDescription = message.getStringProperty(Constants.DESCRIPTION);

      notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
      notificationType = NotificationType.valueOf(actualMessageType);

      dataObject = message.getObject();
    } catch (final JMSException e) {
      log.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      log.debug("correlationUid: {}", correlationUid);
      log.debug("messageType: {}", actualMessageType);
      log.debug("organisationIdentification: {}", organisationIdentification);
      log.debug("deviceIdentification: {}", deviceIdentification);
      return;
    }

    log.info(
        "Calling application service function to handle response: {} with correlationUid: {}",
        actualMessageType,
        correlationUid);

    final CorrelationIds ids =
        new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
    this.handleMessage(ids, actualMessageType, resultType, resultDescription, dataObject);

    try {
      log.info("Send notification for correlationUid: {}", correlationUid);

      // Send notification indicating data is available.
      this.notificationService.sendNotification(
          new ApplicationDataLookupKey(
              organisationIdentification, this.webserviceNotificationApplicationName),
          new GenericNotification(
              notificationMessage,
              resultType.name(),
              deviceIdentification,
              correlationUid,
              String.valueOf(notificationType)));

      log.info("Notification sent for correlationUid: {}", correlationUid);

    } catch (final Exception e) {
      // Logging is enough, sending the notification will be done
      // automatically by the resend notification job
      log.warn(
          "Delivering notification with correlationUid: {} and notification type: {} did not complete successfully.",
          correlationUid,
          notificationType,
          e);
    }
  }

  protected void handleMessage(
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType resultType,
      final String resultDescription,
      final Serializable dataObject) {

    final short numberOfNotificationsSent = 0;
    final Serializable responseObject;
    if (dataObject == null) {
      responseObject = resultDescription;
    } else {
      responseObject = dataObject;
    }

    final ResponseData responseData =
        new ResponseData(ids, messageType, resultType, responseObject, numberOfNotificationsSent);
    this.responseDataService.enqueue(responseData);
  }
}
