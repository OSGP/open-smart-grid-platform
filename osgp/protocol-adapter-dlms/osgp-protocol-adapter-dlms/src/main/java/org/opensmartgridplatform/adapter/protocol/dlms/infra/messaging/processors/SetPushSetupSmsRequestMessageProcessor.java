// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/** Class for processing set push setup sms request messages */
@Component
public class SetPushSetupSmsRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final ConfigurationService configurationService;

  public SetPushSetupSmsRequestMessageProcessor(final ConfigurationService configurationService) {
    super(MessageType.SET_PUSH_SETUP_SMS);
    this.configurationService = configurationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(PushSetupSmsDto.class, requestObject);

    final PushSetupSmsDto pushSetupSms = (PushSetupSmsDto) requestObject;
    this.configurationService.setPushSetupSms(conn, device, pushSetupSms, messageMetadata);
    return null;
  }
}
