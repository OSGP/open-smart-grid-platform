// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.SetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.RtuDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing microgrids set data request messages */
@Component("iec61850MicrogridsSetDataRequestMessageProcessor")
public class MicrogridsSetDataRequestMessageProcessor extends RtuDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MicrogridsSetDataRequestMessageProcessor.class);

  public MicrogridsSetDataRequestMessageProcessor() {
    super(MessageType.SET_DATA);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.info("Processing microgrids set data request message");

    MessageMetadata messageMetadata;
    SetDataRequestDto setDataRequest;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      setDataRequest = (SetDataRequestDto) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    final RequestMessageData requestMessageData =
        RequestMessageData.newBuilder().messageMetadata(messageMetadata).build();

    this.printDomainInfo(requestMessageData);

    final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler =
        this.createIec61850DeviceResponseHandler(requestMessageData, message);

    final DeviceRequest.Builder deviceRequestBuilder =
        DeviceRequest.newBuilder().messageMetaData(messageMetadata);

    this.deviceService.setData(
        new SetDataDeviceRequest(deviceRequestBuilder, setDataRequest),
        iec61850DeviceResponseHandler);
  }
}
