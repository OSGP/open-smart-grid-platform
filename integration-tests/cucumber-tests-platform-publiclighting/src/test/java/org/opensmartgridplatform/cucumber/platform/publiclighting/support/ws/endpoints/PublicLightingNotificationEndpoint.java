// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.endpoints;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.SendNotificationResponse;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingNotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PublicLightingNotificationEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingNotificationEndpoint.class);
  private static final String PUBLIC_LIGHTING_NOTIFICATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/netmanagement/osgp-notification/public-lighting/2019/01";

  @Autowired private PublicLightingNotificationService notificationService;

  public PublicLightingNotificationEndpoint() {
    // Default constructor
  }

  @PayloadRoot(
      localPart = "SendNotificationRequest",
      namespace = PUBLIC_LIGHTING_NOTIFICATION_NAMESPACE)
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
