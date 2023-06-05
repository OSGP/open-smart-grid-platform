// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DecoupleMbusDeviceByChannelRequestMessageProcessor
    extends BaseRequestMessageProcessor {

  private final InstallationService installationService;

  protected DecoupleMbusDeviceByChannelRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringInstallationService")
          final InstallationService installationService) {
    super(messageProcessorMap, MessageType.DECOUPLE_MBUS_DEVICE_BY_CHANNEL);
    this.installationService = installationService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final DecoupleMbusDeviceByChannelRequestData decoupleMbusDeviceByChannelRequest =
        (DecoupleMbusDeviceByChannelRequestData) dataObject;

    this.installationService.decoupleMbusDeviceByChannel(
        deviceMessageMetadata, decoupleMbusDeviceByChannelRequest);
  }
}
