package org.opensmartgridplatform.adapter.ws.admin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import org.opensmartgridplatform.adapter.ws.admin.application.config.AdminNotificationClientConfig;
import org.opensmartgridplatform.adapter.ws.clients.SendNotificationServiceClient;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

/**
 * An instance of this class is created by a bean function in an application
 * context class. See {@link AdminNotificationClientConfig#notificationService()}.
 */
public class AdminNotificationService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminNotificationService.class);

    private static final String CAUGHT_EXCEPTION = "Caught exception when sending notification";

    @Autowired
    private SendNotificationServiceClient sendNotificationServiceClient;

    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message, final Object notificationType) {
        this.sendNotificationForDeviceResponse((NotificationType) notificationType, organisationIdentification,
                deviceIdentification, correlationUid, result, message);
    }

    public void sendNotification(final NotificationType notificationType, final String organisationIdentification,
            final String deviceIdentification) {
        this.doSendNotification(notificationType, organisationIdentification, deviceIdentification, null, null, null);
    }

    public void sendNotificationForDeviceResponse(final NotificationType notificationType,
            final String organisationIdentification, final String deviceIdentification, final String correlationUid,
            final String result, final String message) {
        this.doSendNotification(notificationType, organisationIdentification, deviceIdentification, correlationUid,
                result, message);
    }

    public void sendNotificationAndCatchAllExceptions(final NotificationType notificationType,
            final String organisationIdentification, final String deviceIdentification) {
        try {
            this.doSendNotification(notificationType, organisationIdentification, deviceIdentification, null, null,
                    null);
        } catch (final Exception e) {
            LOGGER.error(CAUGHT_EXCEPTION, e);
        }
    }

    public void sendNotificationForDeviceResponseAndCatchAllExceptions(final NotificationType notificationType,
            final String organisationIdentification, final String deviceIdentification, final String correlationUid,
            final String result, final String message) {
        try {
            this.doSendNotification(notificationType, organisationIdentification, deviceIdentification, correlationUid,
                    result, message);
        } catch (final Exception e) {
            LOGGER.error(CAUGHT_EXCEPTION, e);
        }
    }

    private void doSendNotification(final NotificationType notificationType, final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final String result, final String message) {
        final Notification notification = new Notification();
        // Required fields.
        notification.setNotificationType(notificationType);
        notification.setDeviceIdentification(deviceIdentification);

        // Optional fields.
        if (!StringUtils.isEmpty(correlationUid)) {
            notification.setCorrelationUid(correlationUid);
        }
        if (!StringUtils.isEmpty(result)) {
            notification.setResult(OsgpResultType.valueOf(result));
        }
        if (!StringUtils.isEmpty(message)) {
            notification.setMessage(message);
        }

        // Try to send notification and catch security exceptions.
        try {
            this.sendNotificationServiceClient.sendNotification(organisationIdentification, notification);
        } catch (final WebServiceSecurityException e) {
            LOGGER.error("Unable to send notification", e);
        }
    }
}
