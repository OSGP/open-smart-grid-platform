/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common stop device test request message */
@Component("iec61850CommonStopDeviceTestRequestMessageProcessor")
public class CommonStopDeviceTestRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonStopDeviceTestRequestMessageProcessor.class);

  public CommonStopDeviceTestRequestMessageProcessor() {
    super(MessageType.STOP_SELF_TEST);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common stop device test request message");

    MessageMetadata messageMetadata;
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

    // This is a stop self-test, so startOfTest == false.
    this.deviceService.runSelfTest(deviceRequest, iec61850DeviceResponseHandler, false);
  }
}
