//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.MonitoringService;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing da get pq values response messages */
@Component("domainDistributionAutomationGetMeasurementReportResponseMessageProcessor")
public class GetMeasurementReportResponseMessageProcessor extends BaseNotificationMessageProcessor {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetMeasurementReportResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainDistributionAutomationMonitoringService")
  private MonitoringService monitoringService;

  @Autowired
  protected GetMeasurementReportResponseMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, MessageType.GET_MEASUREMENT_REPORT);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing Measurement Report message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;

    ResponseMessage responseMessage = null;
    ResponseMessageResultType responseMessageResultType = null;
    OsgpException osgpException = null;
    Object dataObject = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      responseMessage = (ResponseMessage) message.getObject();
      responseMessageResultType = responseMessage.getResult();
      osgpException = responseMessage.getOsgpException();
      dataObject = responseMessage.getDataObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("osgpException", osgpException);
      return;
    }
    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      final MeasurementReportDto dataResponse = (MeasurementReportDto) dataObject;

      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);

      this.monitoringService.handleGetMeasurementReportResponse(
          dataResponse, ids, messageType, responseMessageResultType, osgpException);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, messageType);
    }
  }
}
