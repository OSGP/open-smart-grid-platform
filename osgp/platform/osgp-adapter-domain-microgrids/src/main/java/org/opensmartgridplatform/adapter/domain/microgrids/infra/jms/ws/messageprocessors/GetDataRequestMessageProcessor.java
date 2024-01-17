// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.infra.jms.ws.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.microgrids.application.services.AdHocManagementService;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
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

/** Class for processing microgrids get data request messages */
@Component("domainMicrogridsGetDataRequestMessageProcessor")
public class GetDataRequestMessageProcessor extends BaseNotificationMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDataRequestMessageProcessor.class);

  @Autowired
  @Qualifier("domainMicrogridsAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  public GetDataRequestMessageProcessor(
      final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainMicrogridsInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, MessageType.GET_DATA);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.info("Processing public lighting get status request message");

    String correlationUid = null;
    String messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    GetDataRequest dataRequest = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      if (message.getObject() instanceof GetDataRequest) {
        dataRequest = (GetDataRequest) message.getObject();
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

      this.adHocManagementService.getData(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          messageType,
          dataRequest);

    } catch (final Exception e) {
      this.handleError(
          e, correlationUid, organisationIdentification, deviceIdentification, messageType);
    }
  }
}
