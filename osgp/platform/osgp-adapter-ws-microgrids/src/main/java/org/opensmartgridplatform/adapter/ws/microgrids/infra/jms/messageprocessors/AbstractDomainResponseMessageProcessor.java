// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.infra.jms.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.io.Serializable;
import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class AbstractDomainResponseMessageProcessor implements MessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractDomainResponseMessageProcessor.class);

  /** The map of message processor instances. */
  @Autowired
  @Qualifier(value = "wsMicrogridsInboundDomainResponsesMessageProcessorMap")
  protected MessageProcessorMap messageProcessorMap;

  @Autowired private NotificationService notificationService;

  @Autowired private ResponseDataService responseDataService;

  /** The message type that a message processor implementation can handle. */
  protected MessageType messageType;

  /**
   * Construct a message processor instance by passing in the message type.
   *
   * @param messageType The message type a message processor can handle.
   */
  protected AbstractDomainResponseMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.messageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing response message");

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
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
      resultDescription = message.getStringProperty(Constants.DESCRIPTION);

      notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
      notificationType = NotificationType.valueOf(messageType);

      dataObject = message.getObject();
    } catch (final JMSException | IllegalArgumentException | NullPointerException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      return;
    }

    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.handleMessage(ids, messageType, resultType, resultDescription, dataObject);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
      return;
    }

    /*
     * Keep the notification part apart from handling the message. Exception
     * handling that might be appropriate for issues with the message could
     * likely not be appropriate for exceptions in the notification
     * mechanism. The latter kind of issues should be covered by re-sending
     * of notifications for existing response data or by application-side
     * attempts to retrieve results after some amount of time.
     */
    this.sendNotification(
        organisationIdentification,
        deviceIdentification,
        resultType.name(),
        correlationUid,
        notificationMessage,
        notificationType);
  }

  protected void handleMessage(
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType resultType,
      final String resultDescription,
      final Serializable dataObject) {

    final short numberOfNotificationsSent = 0;
    final Serializable responseDataObject;
    if (dataObject == null) {
      responseDataObject = resultDescription;
    } else {
      responseDataObject = dataObject;
    }

    final ResponseData responseData =
        new ResponseData(
            ids, messageType, resultType, responseDataObject, numberOfNotificationsSent);
    this.responseDataService.enqueue(responseData);
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e The exception.
   * @param correlationUid The correlation UID.
   * @param organisationIdentification The organisation identification.
   * @param deviceIdentification The device identification.
   * @param notificationType The message type.
   */
  protected void handleError(
      final Exception e,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final NotificationType notificationType) {

    LOGGER.info("handling error: {} for notification type: {}", e.getMessage(), notificationType);
    this.sendNotification(
        organisationIdentification,
        deviceIdentification,
        "NOT_OK",
        correlationUid,
        e.getMessage(),
        notificationType);
  }

  private void sendNotification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String result,
      final String correlationUid,
      final String message,
      final NotificationType notificationType) {

    /*
     * Make sure exceptions are not thrown out of this method. Exceptions
     * could trigger retries from the message queue, that should not happen
     * when the response has been made available to be retrieved (which
     * should be the case before notifications are sent).
     */

    try {
      this.notificationService.sendNotification(
          new ApplicationDataLookupKey(organisationIdentification, "ZownStream"),
          new GenericNotification(
              message, result, deviceIdentification, correlationUid, notificationType.name()));
    } catch (final RuntimeException e) {
      LOGGER.error(
          "Exception sending notification for {} response data with correlation UID {} and result {}",
          notificationType,
          correlationUid,
          result,
          e);
    }
  }
}
