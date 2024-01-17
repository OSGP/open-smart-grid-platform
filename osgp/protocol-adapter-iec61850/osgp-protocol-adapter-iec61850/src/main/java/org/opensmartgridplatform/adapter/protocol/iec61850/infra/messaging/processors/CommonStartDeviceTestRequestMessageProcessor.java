// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common start device test request messages */
@Component("iec61850CommonStartDeviceTestRequestMessageProcessor")
public class CommonStartDeviceTestRequestMessageProcessor
    extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonStartDeviceTestRequestMessageProcessor.class);

  public CommonStartDeviceTestRequestMessageProcessor() {
    super(MessageType.START_SELF_TEST);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common start device test request message");

    MessageMetadata messageMetadata = null;
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

    // This is a start self-test, so startOfTest == true.
    this.deviceService.runSelfTest(deviceRequest, iec61850DeviceResponseHandler, true);
  }
}
