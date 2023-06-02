//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRequestMessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractRequestMessageProcessor.class);

  private static final String ERROR_MSG_UNSUPPORTED_OPERATION =
      "Operation %s is not supported, it must be " + "overridden by the implementing class.";
  private final ComponentType componentType;

  @Autowired protected NotificationResponseMessageSender responseMessageSender;

  protected AbstractRequestMessageProcessor() {
    this.componentType = ComponentType.DOMAIN_SMART_METERING;
  }

  protected void handleMessage(final MessageMetadata messageMetadata, final Object dataObject)
      throws FunctionalException {
    throw new UnsupportedOperationException(
        String.format(
            ERROR_MSG_UNSUPPORTED_OPERATION, "handleMessage(messageMetadata, dataObject)"));
  }

  protected void handleMessage(final MessageMetadata messageMetadata) throws FunctionalException {
    throw new UnsupportedOperationException(
        String.format(ERROR_MSG_UNSUPPORTED_OPERATION, "handleMessage(messageMetadata)"));
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e The exception.
   * @param messageMetadata The metadata for the message for which an error is handled.
   */
  protected void handleError(final Exception e, final MessageMetadata messageMetadata) {
    final String messageType = messageMetadata.getMessageType();
    LOGGER.error("handling error: {} for message type: {}", e.getMessage(), messageType, e);
    OsgpException osgpException = null;
    if (e instanceof OsgpException) {
      osgpException = (OsgpException) e;
    } else {
      osgpException =
          new TechnicalException(
              this.componentType,
              String.format("An unknown error of type %s occurred.", e.getClass().getName()),
              e);
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .build();
    this.responseMessageSender.send(responseMessage, messageType);
  }
}
