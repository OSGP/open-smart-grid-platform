// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.in.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.NotificationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing smart metering system event request messages. */
@Component
public class SystemEventRequestMessageProcessor extends BaseRequestMessageProcessor {

  @Autowired private NotificationService notificationService;

  @Autowired
  protected SystemEventRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundOsgpCoreRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.SYSTEM_EVENT);
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    this.notificationService.handleSystemEvent(
        deviceMessageMetadata, (SystemEventDto) ((RequestMessage) dataObject).getRequest());
  }
}
