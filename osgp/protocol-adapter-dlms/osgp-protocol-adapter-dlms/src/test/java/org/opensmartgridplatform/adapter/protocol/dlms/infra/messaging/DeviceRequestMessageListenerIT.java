/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests the incoming JMS messages and processors. Verifies that response messages were sent. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingTestConfiguration.class)
public class DeviceRequestMessageListenerIT {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceRequestMessageListenerIT.class);

  @Autowired private DeviceRequestMessageListener listener;

  @Autowired private DeviceResponseMessageSender protocolDlmsOutboundOsgpCoreResponsesMessageSender;

  @Autowired private ThrottlingService throttlingService;

  @Autowired private DomainHelperService domainHelperService;

  @Test
  public void testProcessRequestMessages() throws JMSException, OsgpException {

    // SETUP

    DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification("1");
    dlmsDevice.setIpAddress("127.0.0.1");
    dlmsDevice.setHls5Active(true);

    when(domainHelperService.findDlmsDevice(any(String.class), any(String.class)))
        .thenReturn(dlmsDevice);
    when(domainHelperService.findDlmsDevice(any(MessageMetadata.class))).thenReturn(dlmsDevice);
    doNothing()
        .when(protocolDlmsOutboundOsgpCoreResponsesMessageSender)
        .send(any(ResponseMessage.class));

    // EXECUTE

    LOGGER.info("Starting Test");

    for (int i = 0; i < 200; i++) {

      LOGGER.info("Send message number {} ", i);

      final ObjectMessage message =
          new ObjectMessageBuilder()
              .withDeviceIdentification("osgp")
              .withMessageType(MessageType.GET_PROFILE_GENERIC_DATA.toString())
              .withObject(
                  new GetPowerQualityProfileRequestDataDto("PUBLIC", new Date(), new Date(), null))
              .build();

      listener.onMessage(message);
    }

    verify(protocolDlmsOutboundOsgpCoreResponsesMessageSender, times(200))
        .send(any(ResponseMessage.class));

    assertThat(throttlingService.toString())
        .isEqualTo(
            "ThrottlingService. maxOpenConnections = 10, maxNewConnectionRequests=30, resetTime=2000");
  }
}
