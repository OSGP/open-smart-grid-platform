//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.clients;

import java.net.MalformedURLException;
import java.net.URL;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.SendNotificationRequest;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

/** SOAP client for the notification web service. */
public class SendNotificationServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendNotificationServiceClient.class);

  private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

  private final String organisationIdentification;

  private final String userName;

  /**
   * An instance of this class is created by a bean function in an application context class. For
   * example {@link NotificationClientConfig#sendNotificationServiceClient()}.
   *
   * @param webServiceTemplateFactory An instance of the web service template factory.
   * @param organisationIdentification The organization which will issue the SOAP requests.
   * @param userName The user which will issue the SOAP requests.
   */
  public SendNotificationServiceClient(
      final DefaultWebServiceTemplateFactory webServiceTemplateFactory,
      final String organisationIdentification,
      final String userName) {
    this.webServiceTemplateFactory = webServiceTemplateFactory;
    this.organisationIdentification = organisationIdentification;
    this.userName = userName;
  }

  public void sendNotification(
      final String organisationIdentification, final Notification notification)
      throws WebServiceSecurityException {
    final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
    sendNotificationRequest.setNotification(notification);

    String organisation;
    if (StringUtils.hasText(organisationIdentification)) {
      organisation = organisationIdentification;
    } else {
      organisation = this.organisationIdentification;
    }

    final WebServiceTemplate template =
        this.webServiceTemplateFactory.getTemplate(organisation, this.userName);
    template.marshalSendAndReceive(sendNotificationRequest);
  }

  public void sendNotification(
      final String organisationIdentification,
      final Notification notification,
      final String notificationURL,
      final String notificationUsername)
      throws WebServiceSecurityException {

    final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
    sendNotificationRequest.setNotification(notification);

    try {
      this.webServiceTemplateFactory
          .getTemplate(organisationIdentification, notificationUsername, new URL(notificationURL))
          .marshalSendAndReceive(sendNotificationRequest);
    } catch (final MalformedURLException e) {
      LOGGER.error("Unexpected exception by creating notification URL", e);
    }
  }
}
