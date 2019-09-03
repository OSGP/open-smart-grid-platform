package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

    @Test
    public void testProcessMessageSuccess() throws JMSException, UnknownEntityException {

        final String deviceIdentification = "dvc-1";
        final String correlationUid = "corr-uid-1";
        final String organisationIdentification = "test-org";
        final String ipAddress = "127.0.0.1";

        RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, ipAddress, pushNotificationSms);

        ObjectMessage message = new ObjectMessageBuilder().withCorrelationUid(correlationUid).withMessageType(
                MessageType.PUSH_NOTIFICATION_SMS.name()).withDeviceIdentification(deviceIdentification).withObject(
                requestMessage).build();

        when(pushNotificationSms.getIpAddress()).thenReturn(ipAddress);
        doNothing().when(eventNotificationMessageService).handleEvent(any(String.class), any(Date.class),
                any(EventType.class), any(String.class), any(Integer.class));

        Device device = new Device(deviceIdentification);

        when(deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(device);

        assertNull(device.getLastSuccessfulConnectionTimestamp());

        pushNotificationSmsMessageProcessor.processMessage(message);

        assertNotNull(device.getLastSuccessfulConnectionTimestamp());
    }

}
