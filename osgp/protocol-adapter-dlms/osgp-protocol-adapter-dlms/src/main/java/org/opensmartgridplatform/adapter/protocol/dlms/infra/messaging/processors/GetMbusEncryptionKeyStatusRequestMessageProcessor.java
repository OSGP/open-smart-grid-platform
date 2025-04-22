// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/** Class for processing the get M-Bus encryption keys status request message */
@Component
public class GetMbusEncryptionKeyStatusRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  private final ConfigurationService configurationService;

  public GetMbusEncryptionKeyStatusRequestMessageProcessor(
      final ConfigurationService configurationService) {
    super(MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS);
    this.configurationService = configurationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(GetMbusEncryptionKeyStatusRequestDto.class, requestObject);
    final GetMbusEncryptionKeyStatusRequestDto request =
        (GetMbusEncryptionKeyStatusRequestDto) requestObject;
    return this.configurationService.requestGetMbusEncryptionKeyStatus(
        conn, device, request, messageMetadata);
  }
}
