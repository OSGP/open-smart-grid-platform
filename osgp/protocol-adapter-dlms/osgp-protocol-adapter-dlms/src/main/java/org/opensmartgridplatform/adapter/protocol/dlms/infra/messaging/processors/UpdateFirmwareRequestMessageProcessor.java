/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UpdateFirmwareRequestMessageProcessor.class);

  @Autowired private ConfigurationService configurationService;

  @Autowired private FirmwareService firmwareService;

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  protected UpdateFirmwareRequestMessageProcessor() {
    super(MessageType.UPDATE_FIRMWARE);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String organisationIdentification = messageMetadata.getOrganisationIdentification();
    final String correlationUid = messageMetadata.getCorrelationUid();
    final String messageType = messageMetadata.getMessageType();

    this.assertRequestObjectType(String.class, requestObject);

    LOGGER.info(
        "{} called for device: {} for organisation: {}",
        messageType,
        deviceIdentification,
        organisationIdentification);

    final String firmwareIdentification = (String) requestObject;

    if (this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)) {
      LOGGER.info(
          "[{}] - Firmware file [{}] available. Updating firmware on device [{}]",
          correlationUid,
          firmwareIdentification,
          deviceIdentification);
      return this.configurationService.updateFirmware(
          conn, device, firmwareIdentification, messageMetadata);
    } else {
      LOGGER.info(
          "[{}] - Firmware file [{}] not available. Sending GetFirmwareFile request to core.",
          correlationUid,
          firmwareIdentification);
      final RequestMessage message =
          new RequestMessage(
              correlationUid,
              organisationIdentification,
              deviceIdentification,
              firmwareIdentification);
      this.osgpRequestMessageSender.send(
          message, MessageType.GET_FIRMWARE_FILE.name(), messageMetadata);
      return NO_RESPONSE;
    }
  }
}
