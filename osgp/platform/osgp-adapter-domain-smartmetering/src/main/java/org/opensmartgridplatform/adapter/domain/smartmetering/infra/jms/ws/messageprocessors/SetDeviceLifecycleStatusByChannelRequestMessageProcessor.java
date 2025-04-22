// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ManagementService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceLifecycleStatusByChannelRequestMessageProcessor
    extends BaseRequestMessageProcessor {

  private final ManagementService managementService;

  protected SetDeviceLifecycleStatusByChannelRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringManagementService")
          final ManagementService managementService) {
    super(messageProcessorMap, MessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL);
    this.managementService = managementService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final SetDeviceLifecycleStatusByChannelRequestData requestData =
        (SetDeviceLifecycleStatusByChannelRequestData) dataObject;

    this.managementService.setDeviceLifecycleStatusByChannel(deviceMessageMetadata, requestData);
  }
}
