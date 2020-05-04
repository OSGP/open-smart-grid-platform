/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import stub.DeviceResponseMessageSenderStub;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingTestConfiguration.class)
public class DeviceRequestMessageListenerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListenerIT.class);

    @Autowired
    private DeviceRequestMessageListener listener;

    @Autowired
    private DeviceResponseMessageSenderStub deviceResponseMessageSender;

    @Autowired
    private ThrottlingService throttlingService;

    @Test
    public void testManyMessages() throws JMSException {

        System.out.println("Starting Test ");

        LOGGER.info("Starting Test");

        for (int i = 0; i < 200; i++) {

            LOGGER.info("Starting Test " + i);

            final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification("osgp").withMessageType(
                    MessageType.GET_PROFILE_GENERIC_DATA.toString()).withObject(
                    new GetPowerQualityProfileRequestDataDto("PUBLIC", new Date(), new Date(), null)).build();

            listener.onMessage(message);
        }

        assertThat(deviceResponseMessageSender.getResponseMessagesSent()).isEqualTo(200);

        assertThat(throttlingService.toString())
                .isEqualTo("ThrottlingService. maxOpenConnections = 10, maxNewConnectionRequests=30, resetTime=2000");

    }

}
