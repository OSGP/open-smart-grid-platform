// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.DeviceManagementService;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing da get health status request messages */
@Component("domainDistributionAutomationGetHealthStatusRequestMessageProcessor")
public class GetHealthStatusRequestMessageProcessor extends BaseNotificationMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainDistributionAutomationDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  public GetHealthStatusRequestMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, MessageType.GET_HEALTH_STATUS);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.info("Processing DA Get Health Status request message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    GetHealthStatusRequest getHealthStatusRequest = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      if (message.getObject() instanceof GetHealthStatusRequest) {
        getHealthStatusRequest = (GetHealthStatusRequest) message.getObject();
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

      this.deviceManagementService.getHealthStatus(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          messageType,
          getHealthStatusRequest);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, messageType);
    }
  }
}
