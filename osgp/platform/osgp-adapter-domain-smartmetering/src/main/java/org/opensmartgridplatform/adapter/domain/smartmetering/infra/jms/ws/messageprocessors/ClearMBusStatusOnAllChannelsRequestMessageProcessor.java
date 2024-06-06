// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ManagementService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ClearMBusStatusOnAllChannelsRequestMessageProcessor
    extends BaseRequestMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringManagementService")
  private ManagementService managementService;

  @Autowired
  protected ClearMBusStatusOnAllChannelsRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.CLEAR_MBUS_STATUS_ON_ALL_CHANNELS);
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final ClearMBusStatusOnAllChannelsRequestData clearMBusStatusOnAllChannelsRequestData =
        (ClearMBusStatusOnAllChannelsRequestData) dataObject;

    this.managementService.clearMBusStatusOnAllChannels(
        deviceMessageMetadata, clearMBusStatusOnAllChannelsRequestData);
  }
}
