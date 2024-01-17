// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import java.util.Optional;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("domainDistributionAutomationGetDataResponseMessageProcessor")
public class GetDataResponseMessageProcessor extends BaseNotificationMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetDataResponseMessageProcessor.class);
  private static final MessageType GET_DATA = MessageType.GET_DATA;

  @Autowired
  @Qualifier("domainDistributionAutomationAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  @Autowired
  protected GetDataResponseMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, GET_DATA);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing DA GET_DATA response message");
    this.getResponseValues(message).ifPresent(this::processResponseValues);
  }

  private Optional<ResponseMessage> getResponseValues(final ObjectMessage message) {
    try {
      if (message.getObject() == null) {
        LOGGER.error("UNRECOVERABLE ERROR, the message object is null, giving up.");
        return Optional.empty();
      }

      if (!(message.getObject() instanceof ResponseMessage)) {
        LOGGER.error(
            "UNRECOVERABLE ERROR, the message object is not a ResponseMessage instance, giving up.");
        return Optional.empty();
      }

      return Optional.of((ResponseMessage) message.getObject());
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return Optional.empty();
    }
  }

  private void processResponseValues(final ResponseMessage response) {
    try {
      this.adHocManagementService.handleGetDataResponse(response, GET_DATA);
    } catch (final RuntimeException e) {
      this.handleError(
          e,
          response.getCorrelationUid(),
          response.getOrganisationIdentification(),
          response.getDeviceIdentification(),
          GET_DATA.toString());
    }
  }
}
