// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common get status request messages */
@Component("iec61850CommonGetStatusRequestMessageProcessor")
public class CommonGetStatusRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonGetStatusRequestMessageProcessor.class);

  public CommonGetStatusRequestMessageProcessor() {
    super(MessageType.GET_STATUS);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common get status request message");

    final MessageMetadata messageMetadata;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    final RequestMessageData requestMessageData =
        RequestMessageData.newBuilder().messageMetadata(messageMetadata).build();

    this.printDomainInfo(requestMessageData);

    final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler =
        this.createIec61850DeviceResponseHandler(requestMessageData, message);

    final DeviceRequest deviceRequest =
        DeviceRequest.newBuilder().messageMetaData(messageMetadata).build();

    this.deviceService.getStatus(deviceRequest, iec61850DeviceResponseHandler);
  }

  @Override
  public void handleDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {
    LOGGER.info("Override for handleDeviceResponse() by CommonGetStatusRequestMessageProcessor");
    this.handleGetStatusDeviceResponse(
        deviceResponse,
        responseMessageSender,
        domainInformation,
        messageType,
        retryCount,
        isScheduled);
  }
}
