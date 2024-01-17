// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.infra.jms.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.io.Serializable;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class to process incoming domain responses. */
@Component(value = "wsDistributionAutomationInboundDomainResponsesMessageProcessor")
public class DomainResponseMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

  @Autowired private String webserviceNotificationOrganisation;

  @Autowired private NotificationService notificationService;

  @Autowired private ResponseDataService responseDataService;

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing distribution automation response message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;

    final String notificationMessage;
    final NotificationType notificationType;
    final ResponseMessageResultType resultType;
    final String resultDescription;
    final Serializable dataObject;

    try {
      correlationUid = message.getJMSCorrelationID();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      messageType = message.getJMSType();
      validateMessageType(messageType);

      resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
      resultDescription = message.getStringProperty(Constants.DESCRIPTION);

      notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
      notificationType = NotificationType.valueOf(messageType);

      dataObject = message.getObject();
    } catch (final IllegalArgumentException e) {
      LOGGER.error("UNRECOVERABLE ERROR, received messageType {} is unknown.", messageType, e);
      logDebugInformation(
          messageType, correlationUid, organisationIdentification, deviceIdentification);

      return;
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      logDebugInformation(
          messageType, correlationUid, organisationIdentification, deviceIdentification);

      return;
    }

    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.handleMessage(ids, messageType, resultType, resultDescription, dataObject);

      // Send notification indicating data is available.
      this.notificationService.sendNotification(
          this.webserviceNotificationOrganisation,
          deviceIdentification,
          resultType.name(),
          correlationUid,
          notificationMessage,
          notificationType);

    } catch (final RuntimeException e) {
      handleError(e, correlationUid, notificationType);
    }
  }

  private void handleMessage(
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType resultType,
      final String resultDescription,
      final Serializable dataObject) {

    final short NUMBER_OF_NOTIFICATIONS_SENT = 0;

    final Serializable responseObject;
    if (dataObject == null) {
      responseObject = resultDescription;
    } else {
      responseObject = dataObject;
    }

    final ResponseData responseData =
        new ResponseData(
            ids, messageType, resultType, responseObject, NUMBER_OF_NOTIFICATIONS_SENT);
    this.responseDataService.enqueue(responseData);
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e The exception.
   * @param correlationUid The correlation UID.
   * @param notificationType The message type.
   */
  private static void handleError(
      final RuntimeException e,
      final String correlationUid,
      final NotificationType notificationType) {

    LOGGER.warn(
        "Error '{}' occurred while trying to send notification type: {} with correlationUid: {}",
        e.getMessage(),
        notificationType,
        correlationUid,
        e);
  }

  /**
   * Checks if a given messageType has a known {@link NotificationType}.
   *
   * @param messageType The messageType to check.
   * @throws IllegalArgumentException, when no NotificationType is found for the given messageType.
   */
  private static void validateMessageType(final String messageType) {
    final NotificationType notificationType = NotificationType.valueOf(messageType);
    LOGGER.debug("Received message has known notification type: \"{}\"", notificationType);
  }

  private static void logDebugInformation(
      final String messageType,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification) {
    LOGGER.debug("messageType: {}", messageType);
    LOGGER.debug("CorrelationUid: {}", correlationUid);
    LOGGER.debug("organisationIdentification: {}", organisationIdentification);
    LOGGER.debug("deviceIdentification: {}", deviceIdentification);
  }
}
