/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.DefaultDeviceResponseService;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.ScheduleManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing public lighting set schedule response messages */
@Component("domainPublicLightingSetScheduleResponseMessageProcessor")
public class PublicLightingSetScheduleResponseMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetScheduleResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainPublicLightingScheduleManagementService")
  private ScheduleManagementService scheduleManagementService;

  @Autowired
  @Qualifier("domainPublicLightingDefaultDeviceResponseService")
  private DefaultDeviceResponseService defaultDeviceResponseService;

  @Autowired
  protected PublicLightingSetScheduleResponseMessageProcessor(
      final ResponseMessageSender webServiceResponseMessageSender,
      @Qualifier("domainPublicLightingInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap osgpCoreResponseMessageProcessorMap) {
    super(
        webServiceResponseMessageSender,
        osgpCoreResponseMessageProcessorMap,
        MessageType.SET_LIGHT_SCHEDULE,
        ComponentType.DOMAIN_PUBLIC_LIGHTING);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing public lighting set schedule response message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;

    ResponseMessage responseMessage;
    ResponseMessageResultType responseMessageResultType = null;
    OsgpException osgpException = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      responseMessage = (ResponseMessage) message.getObject();
      responseMessageResultType = responseMessage.getResult();
      osgpException = responseMessage.getOsgpException();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("messagePriority: {}", messagePriority);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("osgpException", osgpException);
      return;
    }

    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      // If the set light schedule response is OK, set hasSchedule to
      // true.
      this.scheduleManagementService.setHasSchedule(
          deviceIdentification, responseMessageResultType == ResponseMessageResultType.OK);

      // Then send a default response message to web service adapter.
      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.defaultDeviceResponseService.handleDefaultDeviceResponse(
          ids, messageType, messagePriority, responseMessageResultType, osgpException);

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
