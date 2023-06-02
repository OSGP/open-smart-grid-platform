//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ReadAlarmRegisterResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  @Autowired
  protected ReadAlarmRegisterResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.READ_ALARM_REGISTER,
        ComponentType.DOMAIN_SMART_METERING);
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    return responseMessage.getDataObject() instanceof AlarmRegisterResponseDto;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException) {

    final AlarmRegisterResponseDto alarmRegisterDto =
        (AlarmRegisterResponseDto) responseMessage.getDataObject();

    this.monitoringService.handleReadAlarmRegisterResponse(
        deviceMessageMetadata, responseMessage.getResult(), osgpException, alarmRegisterDto);
  }
}
