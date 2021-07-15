/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870LightMeasurementService implements LightMeasurementService {

  @Autowired private DeviceResponseMessageSender deviceResponseMessageSender;

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  @Override
  public void sendSensorStatus(
      final LightSensorStatusDto lightSensorSatusDto, final ResponseMetadata responseMetadata) {
    final DeviceMessageMetadata deviceMessageMetadata =
        DeviceMessageMetadata.newBuilder()
            .withBypassRetry(true)
            .withCorrelationUid(responseMetadata.getCorrelationUid())
            .withDeviceIdentification(responseMetadata.getDeviceIdentification())
            .withMessageType(responseMetadata.getMessageType())
            .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
            .build();
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .deviceMessageMetadata(deviceMessageMetadata)
            .domain(responseMetadata.getDomainInfo().getDomain())
            .domainVersion(responseMetadata.getDomainInfo().getDomainVersion())
            .dataObject(lightSensorSatusDto)
            .result(ResponseMessageResultType.OK)
            .build();
    this.deviceResponseMessageSender.send(responseMessage);
  }

  @Override
  public void sendEventNotification(
      final EventNotificationDto eventNotification, final ResponseMetadata responseMetadata) {

    final DeviceMessageMetadata deviceMessageMetadata =
        DeviceMessageMetadata.newBuilder()
            .withBypassRetry(true)
            .withCorrelationUid(responseMetadata.getCorrelationUid())
            .withDeviceIdentification(responseMetadata.getDeviceIdentification())
            .withMessageType(responseMetadata.getMessageType())
            .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
            .build();

    final ProtocolRequestMessage requestMessage =
        new ProtocolRequestMessage.Builder()
            .deviceMessageMetadata(deviceMessageMetadata)
            .domain(responseMetadata.getDomainInfo().getDomain())
            .domainVersion(responseMetadata.getDomainInfo().getDomainVersion())
            .request(eventNotification)
            .build();

    this.osgpRequestMessageSender.send(requestMessage, responseMetadata.getMessageType());
  }
}
