/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreLogItemJsonMessageCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreLogItemJsonMessageCreator.class);

  public TextMessage getJsonMessage(
      final CoreLogItemRequestMessage coreLogItemRequestMessage, final Session session) {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(coreLogItemRequestMessage);
      textMessage = session.createTextMessage(jsonString);
      textMessage.setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
    } catch (final Exception e) {
      LOGGER.error("Error creating json message : {}", e.getMessage());
    }
    return textMessage;
  }
}
