// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/** Class for processing Set M-Bus User Key By Channel request messages */
@Component
public class SetMbusUserKeyByChannelRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final ConfigurationService configurationService;

  public SetMbusUserKeyByChannelRequestMessageProcessor(
      final ConfigurationService configurationService) {
    super(MessageType.SET_MBUS_USER_KEY_BY_CHANNEL);
    this.configurationService = configurationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(SetMbusUserKeyByChannelRequestDataDto.class, requestObject);

    return this.configurationService.setMbusUserKeyByChannel(
        conn, device, (SetMbusUserKeyByChannelRequestDataDto) requestObject, messageMetadata);
  }
}
