/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.infra.jms;

import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.UnknownMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "domainAdminInboundOsgpCoreRequestsMessageProcessor")
public class OsgpCoreRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

  public void processMessage(final RequestMessage requestMessage, final String messageType)
      throws UnknownMessageTypeException {

    final String organisationIdentification = requestMessage.getOrganisationIdentification();
    final String deviceIdentification = requestMessage.getDeviceIdentification();
    final String correlationUid = requestMessage.getCorrelationUid();
    final Object dataObject = requestMessage.getRequest();

    LOGGER.info(
        "Received request message from OSGP-CORE messageType: {} deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}, className: {}",
        messageType,
        deviceIdentification,
        organisationIdentification,
        correlationUid,
        dataObject.getClass().getCanonicalName());

    throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
  }
}
