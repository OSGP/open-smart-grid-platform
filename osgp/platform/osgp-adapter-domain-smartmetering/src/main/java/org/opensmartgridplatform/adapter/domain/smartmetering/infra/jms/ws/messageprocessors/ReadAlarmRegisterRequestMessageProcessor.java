// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ReadAlarmRegisterRequestMessageProcessor extends BaseRequestMessageProcessor {

  private final MonitoringService monitoringService;

  public ReadAlarmRegisterRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringMonitoringService")
          final MonitoringService monitoringService) {
    super(messageProcessorMap, MessageType.READ_ALARM_REGISTER);
    this.monitoringService = monitoringService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final ReadAlarmRegisterRequest readAlarmRegisterRequest = (ReadAlarmRegisterRequest) dataObject;

    this.monitoringService.requestReadAlarmRegister(
        deviceMessageMetadata, readAlarmRegisterRequest);
  }
}
