// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.ScheduleManagementService;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing public lighting set schedule request messages */
@Component("domainPublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainPublicLightingScheduleManagementService")
  private ScheduleManagementService scheduleManagementService;

  @Autowired
  public PublicLightingSetScheduleRequestMessageProcessor(
      final WebServiceResponseMessageSender webServiceResponseMessageSender,
      @Qualifier("domainPublicLightingInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap webServiceRequestMessageProcessorMap) {
    super(
        webServiceResponseMessageSender,
        webServiceRequestMessageProcessorMap,
        MessageType.SET_LIGHT_SCHEDULE,
        ComponentType.DOMAIN_PUBLIC_LIGHTING);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing public lighting set schedule request message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;
    final Object dataObject;
    Long scheduleTime = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      if (message.propertyExists(Constants.SCHEDULE_TIME)) {
        scheduleTime = message.getLongProperty(Constants.SCHEDULE_TIME);
      }
      dataObject = message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("messagePriority: {}", messagePriority);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      return;
    }

    try {
      LOGGER.info("Calling application service function: {}", messageType);

      final Schedule schedule = (Schedule) dataObject;
      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.scheduleManagementService.setLightSchedule(
          ids, schedule, scheduleTime, messageType, messagePriority);

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
