package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingTestConfiguration.class)
@ActiveProfiles("test")
public class DeviceRequestMessageProcessorTest {

    //@Rule
    //public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessorTest.class);

    @Autowired
    private DeviceRequestMessageListener listener;

    @BeforeAll
    static void initAll() {
        System.out.println("---Inside initAll---");
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        System.out.println("Start..." + testInfo.getDisplayName());
    }

    @Test
    public void testManyMessages() throws JMSException {

        System.out.println("Starting Test ");

        LOGGER.info("Starting Test");

        for (int i = 0; i < 10; i++) {

            LOGGER.info("Starting Test " + i);

            final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification("osgp").withMessageType(
                    MessageType.GET_PROFILE_GENERIC_DATA.toString()).withObject(new GetPowerQualityProfileResponseDto())
                                                                    .build();

            listener.onMessage(message);
        }

    }

}
