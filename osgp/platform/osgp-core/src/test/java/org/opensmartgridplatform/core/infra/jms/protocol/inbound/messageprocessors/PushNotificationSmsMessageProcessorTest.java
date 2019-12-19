/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors.PushNotificationSmsMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@RunWith(MockitoJUnitRunner.class)
public class PushNotificationSmsMessageProcessorTest {

    @Mock
    private PushNotificationSmsDto pushNotificationSms;

    @Mock
    private EventNotificationMessageService eventNotificationMessageService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private PushNotificationSmsMessageProcessor pushNotificationSmsMessageProcessor;

    private static final String DEVICE_IDENTIFICATION = "dvc-1";
    private static final String IP_ADDRESS = "127.0.0.1";
    private ObjectMessage message;

    @Before
    public void init() throws JMSException {
        final String correlationUid = "corr-uid-1";
        final String organisationIdentification = "test-org";

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                DEVICE_IDENTIFICATION, IP_ADDRESS, this.pushNotificationSms);

        this.message = new ObjectMessageBuilder().withCorrelationUid(correlationUid)
                .withMessageType(MessageType.PUSH_NOTIFICATION_SMS.name())
                .withDeviceIdentification(DEVICE_IDENTIFICATION)
                .withObject(requestMessage)
                .build();
    }

    @Test
    public void testProcessMessageSuccess() throws JMSException, UnknownEntityException {

        when(this.pushNotificationSms.getIpAddress()).thenReturn(IP_ADDRESS);
        doNothing().when(this.eventNotificationMessageService)
                .handleEvent(any(String.class), any(Date.class), any(EventType.class), any(String.class),
                        any(Integer.class));

        final Device device = new Device(DEVICE_IDENTIFICATION);

        when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION)).thenReturn(device);

        assertThat(device.getLastSuccessfulConnectionTimestamp()).isNull();

        this.pushNotificationSmsMessageProcessor.processMessage(this.message);

        assertThat(device.getLastSuccessfulConnectionTimestamp()).isNotNull();

        verify(this.deviceRepository).save(device);
    }

    @Test(expected = JMSException.class)
    public void testUnknownDevice() throws JMSException {

        when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION)).thenReturn(null);
        this.pushNotificationSmsMessageProcessor.processMessage(this.message);
    }

}
