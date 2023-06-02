//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing public lighting set schedule request messages */
@Component("iec61850PublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor
    extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

  public PublicLightingSetScheduleRequestMessageProcessor() {
    super(MessageType.SET_LIGHT_SCHEDULE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing public lighting set schedule request message");

    MessageMetadata messageMetadata;
    ScheduleDto scheduleDto;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      scheduleDto = (ScheduleDto) message.getObject();
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

    this.deviceService.setSchedule(
        new SetScheduleDeviceRequest(deviceRequestBuilder, scheduleDto, RelayTypeDto.LIGHT),
        iec61850DeviceResponseHandler);
  }
}
