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
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.AdhocService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetSpecificAttributeValueRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  @Autowired private AdhocService adhocService;

  protected GetSpecificAttributeValueRequestMessageProcessor() {
    super(MessageType.GET_SPECIFIC_ATTRIBUTE_VALUE);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws ProtocolAdapterException, FunctionalException {

    this.assertRequestObjectType(SpecificAttributeValueRequestDto.class, requestObject);

    final SpecificAttributeValueRequestDto specificConfigurationObjectRequestDataDto =
        (SpecificAttributeValueRequestDto) requestObject;

    return this.adhocService.getSpecificAttributeValue(
        conn, device, specificConfigurationObjectRequestDataDto);
  }
}
