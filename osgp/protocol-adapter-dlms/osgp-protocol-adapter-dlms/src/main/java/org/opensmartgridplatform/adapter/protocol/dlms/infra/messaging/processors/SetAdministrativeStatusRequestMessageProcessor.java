/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing the Set Administrative Status request message */
@Component
public class SetAdministrativeStatusRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private ConfigurationService configurationService;

  public SetAdministrativeStatusRequestMessageProcessor() {
    super(MessageType.SET_ADMINISTRATIVE_STATUS);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {

    this.assertRequestObjectType(AdministrativeStatusTypeDto.class, requestObject);

    final AdministrativeStatusTypeDto administrativeStatusType =
        (AdministrativeStatusTypeDto) requestObject;

    this.configurationService.requestSetAdministrativeStatus(
        conn, device, administrativeStatusType);
    return null;
  }
}
