//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.MonitoringService;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing da get pq values periodic request messages */
@Component("domainDistributionAutomationGetPQValuesPeriodicRequestMessageProcessor")
public class GetPQValuesPeriodicRequestMessageProcessor extends BaseNotificationMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPQValuesPeriodicRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainDistributionAutomationMonitoringService")
  private MonitoringService monitoringService;

  @Autowired
  public GetPQValuesPeriodicRequestMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender, messageProcessorMap, MessageType.GET_POWER_QUALITY_VALUES_PERIODIC);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.info("Processing DA Get PQ Values Periodic request message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    GetPQValuesPeriodicRequest getPQValuesPeriodicRequest = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      if (message.getObject() instanceof GetPQValuesPeriodicRequest) {
        getPQValuesPeriodicRequest = (GetPQValuesPeriodicRequest) message.getObject();
      }

    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      return;
    }

    try {
      LOGGER.info("Calling application service function: {}", messageType);

      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);

      this.monitoringService.getPQValuesPeriodic(ids, messageType, getPQValuesPeriodicRequest);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, messageType);
    }
  }
}
