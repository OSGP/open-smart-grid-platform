// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.application.services.DeviceManagementService;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
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

/** Class for processing common update device ssl certification request messages */
@Component("domainCoreCommonUpdateDeviceSslCertificationRequestMessageProcessor")
public class CommonUpdateDeviceSslCertificationRequestMessageProcessor
    extends BaseMessageProcessor {

  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonUpdateDeviceSslCertificationRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainCoreDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  public CommonUpdateDeviceSslCertificationRequestMessageProcessor(
      @Qualifier("domainCoreOutboundWebServiceResponsesMessageSender")
          final ResponseMessageSender responseMessageSender,
      @Qualifier("domainCoreInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.UPDATE_DEVICE_SSL_CERTIFICATION,
        ComponentType.DOMAIN_CORE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing update device ssl certification message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

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
      final Certification certification = (Certification) message.getObject();

      LOGGER.info("Calling application service function: {}", messageType);

      this.deviceManagementService.updateDeviceSslCertification(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          certification,
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
