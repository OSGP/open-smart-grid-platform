//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.tariffswitching.application.services.ScheduleManagementService;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing tariff switching set schedule request messages */
@Component("domainTariffSwitchingSetScheduleRequestMessageProcessor")
public class TariffSwitchingSetScheduleRequestMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(TariffSwitchingSetScheduleRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainTariffSwitchingScheduleManagementService")
  private ScheduleManagementService scheduleManagementService;

  @Autowired
  public TariffSwitchingSetScheduleRequestMessageProcessor(
      final ResponseMessageSender responseMessageSender,
      @Qualifier("domainTariffSwitchingInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.SET_TARIFF_SCHEDULE,
        ComponentType.DOMAIN_TARIFF_SWITCHING);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing tariff switching set schedule request message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;
    Long scheduleTime = null;
    Object dataObject;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      dataObject = message.getObject();
      if (message.propertyExists(Constants.SCHEDULE_TIME)) {
        scheduleTime = message.getLongProperty(Constants.SCHEDULE_TIME);
      }
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

      final Schedule tariffSchedule = (Schedule) dataObject;
      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.scheduleManagementService.setTariffSchedule(
          ids, tariffSchedule.getScheduleEntries(), scheduleTime, messageType, messagePriority);

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
