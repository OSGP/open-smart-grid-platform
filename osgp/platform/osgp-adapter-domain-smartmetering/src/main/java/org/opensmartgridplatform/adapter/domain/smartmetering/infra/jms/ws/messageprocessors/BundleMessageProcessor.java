// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.BundleService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BundleMessageProcessor extends BaseRequestMessageProcessor {

  private final BundleService bundleService;

  protected BundleMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringBundleService") final BundleService bundleService) {
    super(messageProcessorMap, MessageType.HANDLE_BUNDLED_ACTIONS);
    this.bundleService = bundleService;
  }

  @Override
  protected void handleMessage(final MessageMetadata messageMetadata, final Object dataObject)
      throws FunctionalException {

    final BundleMessageRequest data = (BundleMessageRequest) dataObject;
    this.bundleService.handleBundle(messageMetadata, data);
  }
}
