// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import java.util.Objects;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.BundleService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing find events request messages */
@Component
public class BundleMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private BundleService bundleService;

  public BundleMessageProcessor() {
    super(MessageType.HANDLE_BUNDLED_ACTIONS);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    this.assertRequestObjectType(BundleMessagesRequestDto.class, requestObject);
    final BundleMessagesRequestDto bundleMessagesRequest = (BundleMessagesRequestDto) requestObject;

    return this.bundleService.callExecutors(conn, device, bundleMessagesRequest, messageMetadata);
  }

  /*
   * Overriding shouldRetry to check OK message for retryable FaultResponses.
   */
  @Override
  protected boolean shouldRetry(
      final ResponseMessageResultType result,
      final Exception exception,
      final Serializable responseObject) {
    return super.shouldRetry(result, exception, responseObject)
        || this.shouldRetryBasedOnRetryableFaultResponses(result, responseObject);
  }

  private boolean shouldRetryBasedOnRetryableFaultResponses(
      final ResponseMessageResultType result, final Serializable responseObject) {
    if (result.equals(ResponseMessageResultType.OK)) {
      final BundleMessagesRequestDto response = (BundleMessagesRequestDto) responseObject;
      return response.getActionList().stream()
          .map(ActionDto::getResponse)
          .filter(Objects::nonNull)
          .filter(FaultResponseDto.class::isInstance)
          .anyMatch(f -> ((FaultResponseDto) f).isRetryable());
    }
    return false;
  }
}
