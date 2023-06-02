//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing smart metering default response messages */
@Component
public class AddMeterResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AddMeterResponseMessageProcessor.class);

  @Autowired private InstallationService installationService;

  @Autowired
  protected AddMeterResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.ADD_METER,
        ComponentType.DOMAIN_SMART_METERING);
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    // Only the Result (OK/NOK/Exception) is returned, no need to check the (contents of the
    // dataObject).
    return true;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException) {

    this.installationService.handleAddMeterResponse(
        deviceMessageMetadata, responseMessage.getResult(), osgpException);
  }

  @Override
  protected void handleError(final Exception e, final MessageMetadata deviceMessageMetadata) {
    try {
      this.installationService.removeMeter(deviceMessageMetadata);
    } catch (final Exception ex) {
      LOGGER.error(
          "Error removing meter {} for organization {} from core database with correlation UID {}",
          deviceMessageMetadata.getDeviceIdentification(),
          deviceMessageMetadata.getOrganisationIdentification(),
          deviceMessageMetadata.getCorrelationUid(),
          ex);
    }

    super.handleError(e, deviceMessageMetadata);
  }
}
