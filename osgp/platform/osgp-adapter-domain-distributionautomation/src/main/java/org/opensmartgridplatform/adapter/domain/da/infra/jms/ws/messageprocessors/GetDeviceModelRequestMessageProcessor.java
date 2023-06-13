// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest;
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

/** Class for processing da get device model request messages */
@Component("domainDistributionAutomationGetDeviceModelRequestMessageProcessor")
public class GetDeviceModelRequestMessageProcessor extends BaseNotificationMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDeviceModelRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainDistributionAutomationAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  @Autowired
  public GetDeviceModelRequestMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, MessageType.GET_DEVICE_MODEL);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.info("Processing DA Get Device Model request message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    GetDeviceModelRequest getDeviceModelRequest = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      if (message.getObject() instanceof GetDeviceModelRequest) {
        getDeviceModelRequest = (GetDeviceModelRequest) message.getObject();
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

      this.adHocManagementService.getDeviceModel(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          messageType,
          getDeviceModelRequest);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, messageType);
    }
  }
}
