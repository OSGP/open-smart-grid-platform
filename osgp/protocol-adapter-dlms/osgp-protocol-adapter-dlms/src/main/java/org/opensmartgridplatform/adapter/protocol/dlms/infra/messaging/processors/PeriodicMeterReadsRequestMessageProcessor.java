// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing Periodic Meter Request messages */
@Component
public class PeriodicMeterReadsRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  public PeriodicMeterReadsRequestMessageProcessor() {
    super(MessageType.REQUEST_PERIODIC_METER_DATA);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(PeriodicMeterReadsRequestDto.class, requestObject);

    final PeriodicMeterReadsRequestDto periodicMeterReadsQuery =
        (PeriodicMeterReadsRequestDto) requestObject;

    return this.monitoringService.requestPeriodicMeterReads(
        conn, device, periodicMeterReadsQuery, messageMetadata);
  }
}
