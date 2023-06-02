//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.DefaultDeviceResponseService;
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

/** Class for processing public lighting set transition response messages */
@Component("domainPublicLightingSetTransitionResponseMessageProcessor")
public class PublicLightingSetTransitionResponseMessageProcessor extends BaseMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetTransitionResponseMessageProcessor.class);

  @Autowired
  @Qualifier("domainPublicLightingAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  @Autowired
  @Qualifier("domainPublicLightingDefaultDeviceResponseService")
  private DefaultDeviceResponseService defaultDeviceResponseService;

  @Autowired private Boolean isSetTransitionResponseLoggingEnabled;

  @Autowired
  protected PublicLightingSetTransitionResponseMessageProcessor(
      final ResponseMessageSender webServiceResponseMessageSender,
      @Qualifier("domainPublicLightingInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap osgpCoreResponseMessageProcessorMap) {
    super(
        webServiceResponseMessageSender,
        osgpCoreResponseMessageProcessorMap,
        MessageType.SET_TRANSITION,
        ComponentType.DOMAIN_PUBLIC_LIGHTING);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing public lighting set transition response message");

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

      if (this.isSetTransitionResponseLoggingEnabled) {
        final CorrelationIds ids =
            new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        this.adHocManagementService.handleSetTransitionResponse(
            ids, messageType, messagePriority, responseMessageResultType, osgpException);
      } else {
        final CorrelationIds ids =
            new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        this.defaultDeviceResponseService.handleDefaultDeviceResponse(
            ids, messageType, messagePriority, responseMessageResultType, osgpException);
      }
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
