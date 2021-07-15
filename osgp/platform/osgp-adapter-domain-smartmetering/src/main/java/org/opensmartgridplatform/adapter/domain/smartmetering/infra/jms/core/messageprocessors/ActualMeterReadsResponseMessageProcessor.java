/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ActualMeterReadsResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  @Autowired
  protected ActualMeterReadsResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.REQUEST_ACTUAL_METER_DATA,
        ComponentType.DOMAIN_SMART_METERING);
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    final Object dataObject = responseMessage.getDataObject();
    return dataObject instanceof MeterReadsResponseDto
        || dataObject instanceof MeterReadsGasResponseDto;
  }

  @Override
  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException) {

    if (responseMessage.getDataObject() instanceof MeterReadsResponseDto) {
      final MeterReadsResponseDto actualMeterReadsDto =
          (MeterReadsResponseDto) responseMessage.getDataObject();

      this.monitoringService.handleActualMeterReadsResponse(
          deviceMessageMetadata, responseMessage.getResult(), osgpException, actualMeterReadsDto);
    } else if (responseMessage.getDataObject() instanceof MeterReadsGasResponseDto) {
      final MeterReadsGasResponseDto meterReadsGas =
          (MeterReadsGasResponseDto) responseMessage.getDataObject();
      this.monitoringService.handleActualMeterReadsResponse(
          deviceMessageMetadata, responseMessage.getResult(), osgpException, meterReadsGas);
    }
  }
}
