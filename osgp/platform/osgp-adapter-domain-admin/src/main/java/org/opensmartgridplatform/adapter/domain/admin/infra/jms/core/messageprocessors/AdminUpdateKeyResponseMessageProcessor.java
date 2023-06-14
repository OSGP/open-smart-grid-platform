// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.admin.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.admin.application.services.DeviceManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
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

/** Class for processing admin revoke key messages */
@Component("domainAdminUpdateKeyResponseMessageProcessor")
public class AdminUpdateKeyResponseMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AdminUpdateKeyResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainAdminDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  protected AdminUpdateKeyResponseMessageProcessor(
      @Qualifier("domainAdminOutboundWebServiceResponsesMessageSender")
          final ResponseMessageSender responseMessageSender,
      @Qualifier("domainAdminInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.UPDATE_KEY,
        ComponentType.DOMAIN_CORE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing admin update key response message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;

    ResponseMessage responseMessage = null;
    ResponseMessageResultType responseMessageResultType = null;
    OsgpException osgpException = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      responseMessage = (ResponseMessage) message.getObject();
      responseMessageResultType = responseMessage.getResult();
      osgpException = responseMessage.getOsgpException();
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

      this.deviceManagementService.handleUpdateKeyResponse(
          deviceIdentification,
          organisationIdentification,
          correlationUid,
          messageType,
          responseMessageResultType,
          osgpException);

    } catch (final Exception e) {
      this.handleError(
          e,
          correlationUid,
          organisationIdentification,
          deviceIdentification,
          messageType,
          MessagePriorityEnum.DEFAULT.getPriority());
    }
  }
}
