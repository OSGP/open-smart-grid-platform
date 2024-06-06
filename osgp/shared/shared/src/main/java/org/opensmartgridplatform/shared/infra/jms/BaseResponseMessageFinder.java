// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base class for retrieving response messages from a queue by correlation UID. */
public abstract class BaseResponseMessageFinder {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseResponseMessageFinder.class);

  private static final String JMS_CORRELATION_ID_START = "JMSCorrelationID='";
  private static final String JMS_CORRELATION_ID_END = "'";

  /**
   * Method for retrieving messages by correlation UID.
   *
   * @param correlationUid The correlation UID of the message to find.
   * @return A response message.
   * @throws OsgpException In case an error message is present in the response message.
   */
  public ResponseMessage findMessage(final String correlationUid) throws OsgpException {
    LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);
    final ObjectMessage om = this.receiveObjectMessage(correlationUid);
    ResponseMessage responseJmsMessage;

    if (om != null) {
      LOGGER.info(
          "Message with correlationUID: {} has been found, trying to read message...",
          correlationUid);
      try {
        responseJmsMessage = (ResponseMessage) om.getObject();
        this.checkResponseMessage(responseJmsMessage);
        LOGGER.info("Returning response for message with correlationUID: {}", correlationUid);
      } catch (final JMSException e) {
        LOGGER.error("Error while finding message", e);
        responseJmsMessage = this.createEmptyMessage(correlationUid);
      }
    } else {
      LOGGER.info(
          "No message with correlationUID: {} has been found, NOT_FOUND will be returned.",
          correlationUid);
      responseJmsMessage = this.createEmptyMessage(correlationUid);
    }

    return responseJmsMessage;
  }

  /**
   * Receive an object message from the JMS Template. This method has to be implemented for specific
   * queues by classes that extend this abstract class.
   *
   * @param correlationUid The correlation UID of the message to receive.
   * @return An object message or null.
   */
  protected abstract ObjectMessage receiveObjectMessage(final String correlationUid);

  /**
   * Get a JMSCorrelationID for a correlation UID.
   *
   * @param correlationUid The correlation UID of the message.
   * @return A JMSCorrelationID.
   */
  protected String getJmsCorrelationId(final String correlationUid) {
    return JMS_CORRELATION_ID_START + correlationUid + JMS_CORRELATION_ID_END;
  }

  /**
   * Check if the response message contains an error message.
   *
   * @param responseMessage The response message to check.
   * @throws OsgpException In case an error message is present in the response message.
   */
  protected void checkResponseMessage(final ResponseMessage responseMessage) throws OsgpException {
    if (responseMessage.getResult().equals(ResponseMessageResultType.NOT_OK)
        && responseMessage.getOsgpException() != null) {
      LOGGER.error("Unexpected exception: ", responseMessage.getOsgpException().getCause());
      throw responseMessage.getOsgpException();
    }
  }

  /**
   * Method for creating an empty not found response message.
   *
   * @param correlationUid The correlation UID of the message.
   * @return An empty not found message.
   */
  protected ResponseMessage createEmptyMessage(final String correlationUid) {

    return ResponseMessage.newResponseMessageBuilder()
        .withCorrelationUid(correlationUid)
        .withResult(ResponseMessageResultType.NOT_FOUND)
        .build();
  }
}
