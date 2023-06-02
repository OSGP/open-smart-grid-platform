//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.shared.services;

import java.util.Objects;
import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericSendNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Notification service that sends notifications to endpoints with database configured connection
 * information.
 *
 * <p>This generic service can be used in contexts with notification requests in varying namespaces.
 * A {@link GenericSendNotificationRequest generic notification} is created, which will be mapped to
 * a specific type provided as {@code sendNotificationRequestType} in the constructor using the
 * {@link MapperFacade} provided as {@code mapper}.
 *
 * @param <T> specific type of notification request sent by this service.
 */
public class DefaultNotificationService<T> implements NotificationService {

  private static final String NO_NOTIFICATION_WILL_BE_SENT =
      "no notification will be sent about result {} for device {} with correlationUid {}";
  private static final String NO_TEMPLATE_AVAILABLE =
      "No web service template available for application {} of organisation {}, "
          + NO_NOTIFICATION_WILL_BE_SENT;

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNotificationService.class);

  private final NotificationWebServiceTemplateFactory templateFactory;
  private final Class<T> sendNotificationRequestType;
  private final MapperFacade mapper;

  private final String applicationName;

  /**
   * Notification service class that will send notifications to a web service for which
   * configuration is stored in the database.
   *
   * @param templateFactory web service template factory
   * @param sendNotificationRequestType the specific type of notification request to be sent with
   *     this service
   * @param mapper a mapper capable of mapping a {@link GenericSendNotificationRequest} to the type
   *     specified by {@code sendNotificationRequestType}
   * @see GenericSendNotificationRequest
   */
  public DefaultNotificationService(
      final NotificationWebServiceTemplateFactory templateFactory,
      final Class<T> sendNotificationRequestType,
      final MapperFacade mapper,
      final String applicationName) {

    this.templateFactory =
        Objects.requireNonNull(templateFactory, "templateFactory must not be null");
    this.sendNotificationRequestType =
        Objects.requireNonNull(
            sendNotificationRequestType, "sendNotificationRequestType must not be null");
    this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    this.applicationName = applicationName;
  }

  public DefaultNotificationService(
      final NotificationWebServiceTemplateFactory templateFactory,
      final Class<T> sendNotificationRequestType,
      final MapperFacade mapper) {
    this(templateFactory, sendNotificationRequestType, mapper, "");
  }

  @Override
  public void sendNotification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String result,
      final String correlationUid,
      final String message,
      final Object notificationType) {

    this.sendNotification(
        new ApplicationDataLookupKey(organisationIdentification, this.applicationName),
        new GenericNotification(
            message,
            result,
            deviceIdentification,
            correlationUid,
            String.valueOf(notificationType)));
  }

  @Override
  public void sendNotification(
      final ApplicationDataLookupKey endpointLookupKey, final GenericNotification notification) {

    Objects.requireNonNull(endpointLookupKey, "endpointLookupKey must not be null");
    Objects.requireNonNull(notification, "notification must not be null");

    final WebServiceTemplate template = this.templateFactory.getTemplate(endpointLookupKey);
    if (template == null) {
      LOGGER.warn(
          NO_TEMPLATE_AVAILABLE,
          endpointLookupKey.getApplicationName(),
          endpointLookupKey.getOrganisationIdentification(),
          notification.getResult(),
          notification.getDeviceIdentification(),
          notification.getCorrelationUid());
      return;
    }

    LOGGER.info(
        "sendNotification called with correlationUid: {}, type: {}, to application: {} for organisation: {}",
        notification.getCorrelationUid(),
        notification.getNotificationType(),
        endpointLookupKey.getApplicationName(),
        endpointLookupKey.getOrganisationIdentification());

    final T notificationRequest =
        this.mapper.map(
            new GenericSendNotificationRequest(notification), this.sendNotificationRequestType);

    final String uri = this.getCustomTargetUri(endpointLookupKey, notification);
    if (uri == null) {
      template.marshalSendAndReceive(notificationRequest);
    } else {
      template.marshalSendAndReceive(uri, notificationRequest);
    }
  }
}
