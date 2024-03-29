// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.application.services.DeviceManagementService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
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

/** Class for processing common set device verification key request messages */
@Component("domainCoreCommonSetDeviceLifecycleStatusRequestMessageProcessor")
public class CommonSetDeviceLifecycleStatusRequestMessageProcessor extends BaseMessageProcessor {

  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonSetDeviceLifecycleStatusRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainCoreDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  public CommonSetDeviceLifecycleStatusRequestMessageProcessor(
      @Qualifier("domainCoreOutboundWebServiceResponsesMessageSender")
          final ResponseMessageSender responseMessageSender,
      @Qualifier("domainCoreInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.SET_DEVICE_LIFECYCLE_STATUS,
        ComponentType.DOMAIN_CORE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing common set device verification key message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    DeviceLifecycleStatus deviceLifecycleStatus = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      deviceLifecycleStatus = (DeviceLifecycleStatus) message.getObject();
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

      this.deviceManagementService.setDeviceLifecycleStatus(
          organisationIdentification, deviceIdentification, correlationUid, deviceLifecycleStatus);

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
