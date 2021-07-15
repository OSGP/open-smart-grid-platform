/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetGsmDiagnosticRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private ManagementService managementService;

  protected GetGsmDiagnosticRequestMessageProcessor() {
    super(MessageType.GET_GSM_DIAGNOSTIC);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {

    this.assertRequestObjectType(GetGsmDiagnosticRequestDto.class, requestObject);

    final GetGsmDiagnosticRequestDto requestDto = (GetGsmDiagnosticRequestDto) requestObject;
    return this.managementService.getGsmDiagnostic(conn, device, requestDto);
  }
}
