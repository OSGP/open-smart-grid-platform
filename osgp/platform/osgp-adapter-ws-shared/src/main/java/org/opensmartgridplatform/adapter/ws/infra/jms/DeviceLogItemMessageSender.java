//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class DeviceLogItemMessageSender {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceLogItemMessageSender.class);

  @Autowired
  @Qualifier("loggingJmsTemplate")
  private JmsTemplate loggingJmsTemplate;

  /**
   * Method for sending a logging message to the queue.
   *
   * @param loggingMessage The LoggingRequestMessage request message to send.
   */
  public void send(final LoggingRequestMessage loggingMessage) {
    LOGGER.debug("Sending logger message");
    this.sendMessage(loggingMessage);
  }

  /**
   * Method for sending a logging message to the logger queue.
   *
   * @param loggingMessage The LoggingRequestMessage request message to send.
   */
  private void sendMessage(final LoggingRequestMessage loggingMessage) {
    LOGGER.info("Sending logger message to queue");

    this.loggingJmsTemplate.send(
        new MessageCreator() {
          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setJMSCorrelationID(loggingMessage.getCorrelationUid());
            objectMessage.setLongProperty(
                Constants.TIME_STAMP, loggingMessage.getTimeStamp().getTime());
            objectMessage.setStringProperty(Constants.CLASS_NAME, loggingMessage.getClassName());
            objectMessage.setStringProperty(Constants.METHOD_NAME, loggingMessage.getMethodName());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                loggingMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(Constants.USER_NAME, loggingMessage.getUserName());
            objectMessage.setStringProperty(
                Constants.APPLICATION_NAME, loggingMessage.getApplicationName());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, loggingMessage.getDeviceIdentification());
            objectMessage.setStringProperty(
                Constants.RESPONSE_RESULT, loggingMessage.getResponseResult());
            objectMessage.setIntProperty(
                Constants.RESPONSE_DATA_SIZE, loggingMessage.getResponseDataSize());
            return objectMessage;
          }
        });
  }
}
