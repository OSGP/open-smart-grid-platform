//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.endpoints;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.SendNotificationResponse;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.distributionautomation.NotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DistributionAutomationNotificationEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationNotificationEndpoint.class);
  private static final String DISTRIBUTION_AUTOMATION_NOTIFICATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/distributionautomation/notification/2017/04";

  @Autowired private NotificationService notificationService;

  public DistributionAutomationNotificationEndpoint() {
    // Default constructor
  }

  @PayloadRoot(
      localPart = "SendNotificationRequest",
      namespace = DISTRIBUTION_AUTOMATION_NOTIFICATION_NAMESPACE)
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
