// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class GetGsmDiagnosticRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final ManagementService managementService;

  protected GetGsmDiagnosticRequestMessageProcessor(final ManagementService managementService) {
    super(MessageType.GET_GSM_DIAGNOSTIC);
    this.managementService = managementService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(GetGsmDiagnosticRequestDto.class, requestObject);

    final GetGsmDiagnosticRequestDto requestDto = (GetGsmDiagnosticRequestDto) requestObject;
    return this.managementService.getGsmDiagnostic(conn, device, requestDto, messageMetadata);
  }
}
