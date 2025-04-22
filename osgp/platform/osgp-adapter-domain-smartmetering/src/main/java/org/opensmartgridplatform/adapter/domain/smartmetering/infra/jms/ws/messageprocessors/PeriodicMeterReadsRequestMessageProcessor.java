// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PeriodicMeterReadsRequestMessageProcessor extends BaseRequestMessageProcessor {

  private final MonitoringService monitoringService;

  protected PeriodicMeterReadsRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringMonitoringService")
          final MonitoringService monitoringService) {
    super(messageProcessorMap, MessageType.REQUEST_PERIODIC_METER_DATA);
    this.monitoringService = monitoringService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final PeriodicMeterReadsQuery periodicMeterReadsRequest = (PeriodicMeterReadsQuery) dataObject;

    this.monitoringService.requestPeriodicMeterReads(
        deviceMessageMetadata, periodicMeterReadsRequest);
  }
}
