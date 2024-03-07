// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
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

/** Class for processing public lighting get light sensor status response messages */
@Component("domainPublicLightingGetLightSensorStatusResponseMessageProcessor")
public class PublicLightingGetLightSensorStatusResponseMessageProcessor
    extends BaseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingGetLightSensorStatusResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainPublicLightingAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  @Autowired
  protected PublicLightingGetLightSensorStatusResponseMessageProcessor(
      final ResponseMessageSender webServiceResponseMessageSender,
      @Qualifier("domainPublicLightingInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap osgpCoreResponseMessageProcessorMap) {

    super(
        webServiceResponseMessageSender,
        osgpCoreResponseMessageProcessorMap,
        MessageType.GET_LIGHT_SENSOR_STATUS,
        ComponentType.DOMAIN_PUBLIC_LIGHTING);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing public lighting get light sensor status response message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;

    final ResponseMessage responseMessage;
    ResponseMessageResultType responseMessageResultType = null;
    OsgpException osgpException = null;
    final Object dataObject;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
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
      LOGGER.debug("messagePriority: {}", messagePriority);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("osgpException: ", osgpException);
      return;
    }

    /*
     * Following the way the PublicLightingGetStatusResponseMessageProcessor
     * processes the messages coming through, the code could return here in
     * case the correlationUid equals
     * OsgpSystemCorrelationUid.CORRELATION_UID.
     *
     */

    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      final LightSensorStatusDto lightSensorStatus = (LightSensorStatusDto) dataObject;

      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.adHocManagementService.handleGetLightSensorStatusResponse(
          lightSensorStatus,
          ids,
          messageType,
          messagePriority,
          responseMessageResultType,
          osgpException);

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
