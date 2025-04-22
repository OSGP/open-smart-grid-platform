// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class ActualMeterReadsRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final MonitoringService monitoringService;

  protected ActualMeterReadsRequestMessageProcessor(final MonitoringService monitoringService) {
    super(MessageType.REQUEST_ACTUAL_METER_DATA);
    this.monitoringService = monitoringService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(ActualMeterReadsQueryDto.class, requestObject);

    final ActualMeterReadsQueryDto actualMeterReadsRequest =
        (ActualMeterReadsQueryDto) requestObject;
    return this.monitoringService.requestActualMeterReads(
        conn, device, actualMeterReadsRequest, messageMetadata);
  }
}
