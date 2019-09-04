package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
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
public class PushNotificationAlarmMessageProcessorTest {

    @Mock
    private PushNotificationAlarmDto pushNotificationAlarm;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private EventNotificationMessageService eventNotificationMessageService;

    @Mock
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Mock
    private DeviceAuthorization deviceAuthorization;

    @Mock
    private Organisation organisation;

    @Mock
    private DomainInfoRepository domainInfoRepository;

    @Mock
    private DomainInfo domainInfo;

    @Mock
    private DomainRequestService domainRequestService;

    @InjectMocks
    private PushNotificationAlarmMessageProcessor pushNotificationAlarmMessageProcessor;

    private final String deviceIdentification = "dvc-1";
    private RequestMessage requestMessage;
    private ObjectMessage message;
    private Device device;

    @Before
    public void init() throws JMSException, UnknownEntityException {

        final String correlationUid = "corr-uid-1";
        final String organisationIdentification = "test-org";
        final String ipAddress = "127.0.0.1";

        requestMessage = new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, ipAddress,
                pushNotificationAlarm);

        message = new ObjectMessageBuilder().withCorrelationUid(correlationUid).withMessageType(
                MessageType.PUSH_NOTIFICATION_ALARM.name()).withDeviceIdentification(deviceIdentification).withObject(
                requestMessage).build();

        device = new Device(deviceIdentification);

        when(deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(device);
        when(deviceRepository.save(device)).thenAnswer((Answer<Void>) invocationOnMock -> null);
        doNothing().when(eventNotificationMessageService).handleEvent(any(String.class), any(Date.class),
                any(EventType.class), any(String.class), any(Integer.class));
        when(deviceAuthorizationRepository.findByDeviceAndFunctionGroup(device, DeviceFunctionGroup.OWNER)).thenReturn(
                Collections.singletonList(deviceAuthorization));
        when(deviceAuthorization.getOrganisation()).thenReturn(organisation);
        when(organisation.getOrganisationIdentification()).thenReturn(requestMessage.getOrganisationIdentification());
        when(domainInfoRepository.findAll()).thenReturn(Collections.singletonList(domainInfo));
        when(domainInfo.getDomain()).thenReturn("SMART_METERING");
        when(domainInfo.getDomainVersion()).thenReturn("1.0");
        doNothing().when(domainRequestService).send(any(RequestMessage.class), any(String.class),
                any(DomainInfo.class));
    }

    @Test
    public void testProcessMessageSuccess() throws JMSException {

        assertNull(device.getLastSuccessfulConnectionTimestamp());

        pushNotificationAlarmMessageProcessor.processMessage(message);

        assertNotNull(device.getLastSuccessfulConnectionTimestamp());

        verify(deviceRepository).save(device);

    }

    @Test(expected = JMSException.class)
    public void testUnknownDevice() throws JMSException {

        when(deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
        pushNotificationAlarmMessageProcessor.processMessage(message);
    }

    @Test(expected = JMSException.class)
    public void testUnknownDeviceAuthorization() throws JMSException {

        when(deviceAuthorizationRepository.findByDeviceAndFunctionGroup(device, DeviceFunctionGroup.OWNER)).thenReturn(
                null);
        pushNotificationAlarmMessageProcessor.processMessage(message);
    }
}

