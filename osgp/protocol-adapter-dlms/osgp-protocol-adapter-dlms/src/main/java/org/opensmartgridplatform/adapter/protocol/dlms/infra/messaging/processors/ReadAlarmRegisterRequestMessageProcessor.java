/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadAlarmRegisterRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  protected ReadAlarmRegisterRequestMessageProcessor() {
    super(MessageType.READ_ALARM_REGISTER);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {

    this.assertRequestObjectType(ReadAlarmRegisterRequestDto.class, requestObject);

    final ReadAlarmRegisterRequestDto readAlarmRegisterRequest =
        (ReadAlarmRegisterRequestDto) requestObject;
    return this.monitoringService.requestReadAlarmRegister(conn, device, readAlarmRegisterRequest);
  }
}
