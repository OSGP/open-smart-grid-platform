/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.support.ws.endpoints;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.SendNotificationResponse;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.notification.CoreNotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CoreNotificationEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(CoreNotificationEndpoint.class);
  private static final String CORE_NOTIFICATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/netmanagement/osgp-notification/2018/01";

  @Autowired private CoreNotificationService coreNotificationService;

  public CoreNotificationEndpoint() {
    // Default constructor
  }

  @PayloadRoot(localPart = "SendNotificationRequest", namespace = CORE_NOTIFICATION_NAMESPACE)
  @ResponsePayload
  public SendNotificationResponse sendNotification(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SendNotificationRequest request)
      throws WebServiceException {

    LOGGER.info(
        "Incoming SendNotificationRequest for organisation: {} device: {}.",
        organisationIdentification,
        request.getNotification().getDeviceIdentification());

    this.coreNotificationService.handleNotification(
        request.getNotification(), organisationIdentification);

    return new SendNotificationResponse();
  }
}
