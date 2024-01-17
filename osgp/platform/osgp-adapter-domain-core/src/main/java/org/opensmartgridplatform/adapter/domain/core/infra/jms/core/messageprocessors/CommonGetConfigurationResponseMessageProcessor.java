// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.application.services.ConfigurationManagementService;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing common get configuration response messages */
@Component("domainCoreCommonGetConfigurationResponseMessageProcessor")
public class CommonGetConfigurationResponseMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonGetConfigurationResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainCoreConfigurationManagementService")
  private ConfigurationManagementService configurationManagementService;

  @Autowired
  protected CommonGetConfigurationResponseMessageProcessor(
      @Qualifier("domainCoreOutboundWebServiceResponsesMessageSender")
          final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainCoreInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.GET_CONFIGURATION,
        ComponentType.DOMAIN_CORE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common get configuration response message");

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
      LOGGER.debug("osgpException", osgpException);
      return;
    }

    try {
      LOGGER.info("Calling application service function to handle response: {}", messageType);

      final ConfigurationDto configurationDto = (ConfigurationDto) dataObject;
      final CorrelationIds ids =
          new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
      this.configurationManagementService.handleGetConfigurationResponse(
          configurationDto,
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
