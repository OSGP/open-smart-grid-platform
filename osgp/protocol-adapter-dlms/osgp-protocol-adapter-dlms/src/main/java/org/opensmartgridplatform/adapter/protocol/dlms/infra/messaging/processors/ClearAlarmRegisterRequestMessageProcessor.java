//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing the clear alarm register request message */
@Component
public class ClearAlarmRegisterRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  public ClearAlarmRegisterRequestMessageProcessor() {
    super(MessageType.CLEAR_ALARM_REGISTER);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(ClearAlarmRegisterRequestDto.class, requestObject);

    final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto =
        (ClearAlarmRegisterRequestDto) requestObject;

    this.monitoringService.setClearAlarmRegister(
        conn, device, clearAlarmRegisterRequestDto, messageMetadata);
    return null;
  }
}
