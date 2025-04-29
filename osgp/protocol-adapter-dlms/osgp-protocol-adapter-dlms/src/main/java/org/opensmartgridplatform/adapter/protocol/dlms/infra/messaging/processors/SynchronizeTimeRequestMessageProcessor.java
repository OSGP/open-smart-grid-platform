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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/** Class for processing Synchronize Time Request messages */
@Component
public class SynchronizeTimeRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final AdhocService adhocService;

  public SynchronizeTimeRequestMessageProcessor(final AdhocService adhocService) {
    super(MessageType.SYNCHRONIZE_TIME);
    this.adhocService = adhocService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    this.assertRequestObjectType(SynchronizeTimeRequestDto.class, requestObject);

    this.adhocService.synchronizeTime(
        conn, device, (SynchronizeTimeRequestDto) requestObject, messageMetadata);
    return null;
  }
}
