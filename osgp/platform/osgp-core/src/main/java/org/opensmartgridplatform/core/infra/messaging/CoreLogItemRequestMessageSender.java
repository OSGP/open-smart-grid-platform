/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.messaging;

import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

public class CoreLogItemRequestMessageSender {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CoreLogItemRequestMessageSender.class);

  @Autowired private JmsTemplate coreLogItemRequestsJmsTemplate;

  public void send(final CoreLogItemRequestMessage coreLogItemRequestMessage) {

    LOGGER.debug("Sending CoreLogItemRequestMessage");

    this.coreLogItemRequestsJmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage = session.createObjectMessage();
          objectMessage.setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
          objectMessage.setStringProperty(
              Constants.DECODED_MESSAGE, coreLogItemRequestMessage.getDecodedMessage());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, coreLogItemRequestMessage.getDeviceIdentification());
          if (coreLogItemRequestMessage.hasOrganisationIdentification()) {
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                coreLogItemRequestMessage.getOrganisationIdentification());
          }
          objectMessage.setStringProperty(
              Constants.IS_VALID, coreLogItemRequestMessage.isValid().toString());
          objectMessage.setIntProperty(
              Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
              coreLogItemRequestMessage.getPayloadMessageSerializedSize());
          return objectMessage;
        });
  }
}
