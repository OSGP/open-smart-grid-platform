/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ClearAlarmRegisterRequestMessageProcessor extends BaseRequestMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringMonitoringService")
  private MonitoringService monitoringService;

  @Autowired
  protected ClearAlarmRegisterRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.CLEAR_ALARM_REGISTER);
  }

  @Override
  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final ClearAlarmRegisterRequest clearAlarmRegisterRequest =
        (ClearAlarmRegisterRequest) dataObject;

    this.monitoringService.requestClearAlarmRegister(
        deviceMessageMetadata, clearAlarmRegisterRequest);
  }
}
