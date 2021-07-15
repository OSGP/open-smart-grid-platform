/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.application.services.DeviceManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing common set device verification key request messages */
@Component("domainCoreCommonSetDeviceVerificationKeyRequestMessageProcessor")
public class CommonSetDeviceVerificationKeyRequestMessageProcessor extends BaseMessageProcessor {

  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonSetDeviceVerificationKeyRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainCoreDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  public CommonSetDeviceVerificationKeyRequestMessageProcessor(
      @Qualifier("domainCoreOutboundWebServiceResponsesMessageSender")
          final ResponseMessageSender responseMessageSender,
      @Qualifier("domainCoreInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.SET_DEVICE_VERIFICATION_KEY,
        ComponentType.DOMAIN_CORE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing common set device verification key message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;
    String verificationKey = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      verificationKey = (String) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.info("messagePriority: {}", messagePriority);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      return;
    }

    try {
      LOGGER.info("Calling application service function: {}", messageType);

      this.deviceManagementService.setDeviceVerificationKey(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          verificationKey,
          messageType,
          messagePriority);

    } catch (final Exception e) {
      this.handleError(
          e,
          correlationUid,
          organisationIdentification,
          deviceIdentification,
          messageType,
          messagePriority);
    }
  }
}
