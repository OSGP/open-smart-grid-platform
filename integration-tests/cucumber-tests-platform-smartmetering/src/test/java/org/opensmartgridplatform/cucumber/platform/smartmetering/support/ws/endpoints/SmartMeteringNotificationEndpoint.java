/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.endpoints;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.SendNotificationResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.notification.NotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SmartMeteringNotificationEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SmartMeteringNotificationEndpoint.class);
  private static final String SMARTMETERING_NOTIFICATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-notification/2014/10";

  @Autowired private NotificationService notificationService;

  public SmartMeteringNotificationEndpoint() {
    // Default constructor
  }

  @PayloadRoot(
      localPart = "SendNotificationRequest",
      namespace = SMARTMETERING_NOTIFICATION_NAMESPACE)
  @ResponsePayload
  public SendNotificationResponse sendNotification(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SendNotificationRequest request)
      throws WebServiceException {

    LOGGER.info(
        "Incoming SendNotificationRequest for organisation: {} device: {}.",
        organisationIdentification,
        request.getNotification().getDeviceIdentification());

    this.notificationService.handleNotification(
        request.getNotification(), organisationIdentification);

    return new SendNotificationResponse();
  }
}
