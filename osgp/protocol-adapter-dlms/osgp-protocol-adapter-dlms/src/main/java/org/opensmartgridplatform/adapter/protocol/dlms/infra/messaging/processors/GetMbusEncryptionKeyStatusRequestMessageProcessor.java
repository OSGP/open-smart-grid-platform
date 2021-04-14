/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing the get M-Bus encryption keys status request message */
@Component
public class GetMbusEncryptionKeyStatusRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  @Autowired private ConfigurationService configurationService;

  public GetMbusEncryptionKeyStatusRequestMessageProcessor() {
    super(MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {

    this.assertRequestObjectType(GetMbusEncryptionKeyStatusRequestDto.class, requestObject);
    final GetMbusEncryptionKeyStatusRequestDto request =
        (GetMbusEncryptionKeyStatusRequestDto) requestObject;
    return this.configurationService.requestGetMbusEncryptionKeyStatus(conn, device, request);
  }
}
