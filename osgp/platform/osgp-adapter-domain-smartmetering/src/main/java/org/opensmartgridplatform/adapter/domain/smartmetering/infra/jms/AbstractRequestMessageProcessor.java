/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
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

  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {
    throw new UnsupportedOperationException(
        String.format(
            ERROR_MSG_UNSUPPORTED_OPERATION, "handleMessage(deviceMessageMetadata, dataObject)"));
  }

  protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    throw new UnsupportedOperationException(
        String.format(ERROR_MSG_UNSUPPORTED_OPERATION, "handleMessage(deviceMessageMetadata)"));
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e The exception.
   * @param correlationUid The correlation UID.
   * @param organisationIdentification The organisation identification.
   * @param deviceIdentification The device identification.
   * @param messageType The message type.
   * @param messagePriority The priority of the message.
   */
  protected void handleError(
      final Exception e,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String messageType,
      final int messagePriority) {
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
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .withMessagePriority(messagePriority)
            .build();
    this.responseMessageSender.send(responseMessage, messageType);
  }
}
