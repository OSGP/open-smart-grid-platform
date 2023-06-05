// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.FirmwareLocation;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing common update firmware request messages */
@Component("iec61850CommonUpdateFirmwareRequestMessageProcessor")
public class CommonUpdateFirmwareRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonUpdateFirmwareRequestMessageProcessor.class);

  @Autowired private FirmwareLocation firmwareLocation;

  public CommonUpdateFirmwareRequestMessageProcessor() {
    super(MessageType.UPDATE_FIRMWARE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common update firmware request message");

    MessageMetadata messageMetadata;
    FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      firmwareUpdateMessageDataContainer = (FirmwareUpdateMessageDataContainer) message.getObject();
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

    this.deviceService.updateFirmware(
        new UpdateFirmwareDeviceRequest(
            deviceRequestBuilder,
            this.firmwareLocation.getDomain(),
            this.firmwareLocation.getFullPath(firmwareUpdateMessageDataContainer.getFirmwareUrl()),
            firmwareUpdateMessageDataContainer.getFirmwareModuleData()),
        iec61850DeviceResponseHandler);
  }
}
