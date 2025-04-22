// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class GetMbusEncryptionKeyStatusByChannelRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  private final ConfigurationService configurationService;

  public GetMbusEncryptionKeyStatusByChannelRequestMessageProcessor(
      final ConfigurationService configurationService) {
    super(MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL);
    this.configurationService = configurationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(
        GetMbusEncryptionKeyStatusByChannelRequestDataDto.class, requestObject);
    final GetMbusEncryptionKeyStatusByChannelRequestDataDto request =
        (GetMbusEncryptionKeyStatusByChannelRequestDataDto) requestObject;
    return this.configurationService.requestGetMbusEncryptionKeyStatusByChannel(
        conn, device, request, messageMetadata);
  }
}
