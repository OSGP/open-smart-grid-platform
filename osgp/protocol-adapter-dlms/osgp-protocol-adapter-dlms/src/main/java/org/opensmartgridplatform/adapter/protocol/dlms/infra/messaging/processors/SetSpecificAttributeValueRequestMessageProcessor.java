// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.AdhocService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetSpecificAttributeValueRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class SetSpecificAttributeValueRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  private final AdhocService adhocService;

  protected SetSpecificAttributeValueRequestMessageProcessor(final AdhocService adhocService) {
    super(MessageType.SET_SPECIFIC_ATTRIBUTE_VALUE);
    this.adhocService = adhocService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {

    this.assertRequestObjectType(SetSpecificAttributeValueRequestDto.class, requestObject);

    final SetSpecificAttributeValueRequestDto request =
        (SetSpecificAttributeValueRequestDto) requestObject;

    this.adhocService.setSpecificAttributeValue(conn, device, request, messageMetadata);

    return null;
  }
}
