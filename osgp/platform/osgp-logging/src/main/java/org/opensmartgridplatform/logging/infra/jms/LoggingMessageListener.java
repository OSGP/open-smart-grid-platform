/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.infra.jms;

import java.util.Date;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.logging.domain.entities.MethodResult;
import org.opensmartgridplatform.logging.domain.entities.WebServiceMonitorLogItem;
import org.opensmartgridplatform.logging.domain.repositories.WebServiceMonitorLogRepository;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Fetch incoming log messages from the logging requests queue.
@Component(value = "OsgpLoggingInboundLoggingRequestsMessageListener")
public class LoggingMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMessageListener.class);

  @Autowired private WebServiceMonitorLogRepository webServiceMonitorLogRepository;

  public LoggingMessageListener() {
    // empty constructor
  }

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received logging message");
      final ObjectMessage objectMessage = (ObjectMessage) message;

      // Create a log item.
      final Date timestamp = new Date(objectMessage.getLongProperty(Constants.TIME_STAMP));
      final String organisationIdentification =
          objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      final String deviceIdentification =
          objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      final String correlationUid = objectMessage.getJMSCorrelationID();
      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      final MethodResult methodResult = methodResultFor(objectMessage);
      final WebServiceMonitorLogItem webServiceMonitorLogItem =
          new WebServiceMonitorLogItem(
              timestamp, ids, objectMessage.getStringProperty(Constants.USER_NAME), methodResult);

      // Save the log item in the data base.
      this.webServiceMonitorLogRepository.save(webServiceMonitorLogItem);

    } catch (final JMSException e) {
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
    }
  }

  private static MethodResult methodResultFor(final ObjectMessage objectMessage)
      throws JMSException {
    return new MethodResult(
        objectMessage.getStringProperty(Constants.APPLICATION_NAME),
        objectMessage.getStringProperty(Constants.CLASS_NAME),
        objectMessage.getStringProperty(Constants.METHOD_NAME),
        objectMessage.getStringProperty(Constants.RESPONSE_RESULT),
        objectMessage.getIntProperty(Constants.RESPONSE_DATA_SIZE));
  }
}
