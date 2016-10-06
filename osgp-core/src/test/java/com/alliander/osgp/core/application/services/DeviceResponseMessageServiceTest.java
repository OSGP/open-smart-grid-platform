/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.core.application.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.RetryHeader;

/**
 * test class for DeviceResponseMessageService
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceResponseMessageServiceTest {

    @Mock
    private DomainResponseService domainResponseMessageSender;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @Mock
    private DeviceRequestMessageService deviceRequestMessageService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceResponseMessageService deviceResponseMessageService;

    @Autowired
    private int getMaxRetryCount;

    @Before
    public void initTest() {
        this.getMaxRetryCount = 1;
    }

    private static final DeviceMessageMetadata DEVICE_MESSAGE_DATA = new DeviceMessageMetadata("deviceId",
            "organisationId", "correlationId", "messageType", 4);
    private static final String DOMAIN = "Domain";
    private static final String DOMAIN_VERSION = "1.0";
    private static final String DATA_OBJECT = "data object";
    private static final Timestamp SCHEDULED_TIME = new Timestamp(Calendar.getInstance().getTime().getTime());

    /**
     * test processMessage with a scheduled task that has been successfull
     */
    @Test
    public void testProcessScheduledMessageSuccess() {
        final ResponseMessageResultType result = ResponseMessageResultType.OK;

        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION).result(result)
                .dataObject(DATA_OBJECT).scheduled(true).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);
        scheduledTask.setPending();
        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if message is send and task is deleted
        verify(this.domainResponseMessageSender, times(1)).send(message);
        verify(this.scheduledTaskRepository, times(1)).delete(scheduledTask);
    }

    /**
     * test processMessage with a scheduled task that must be retried
     */
    @Test
    public void testProcessScheduledMessageRetry() {
        final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        final Date scheduledRetryTime = calendar.getTime();

        // since the retryCount is smaller than the maxRetries, the message
        // should be retried
        final RetryHeader retryHeader = new RetryHeader(0, this.getMaxRetryCount, scheduledRetryTime);

        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION).result(result)
                .dataObject(DATA_OBJECT).scheduled(true).retryHeader(retryHeader).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);

        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if the message is not send and the task is not deleted
        verify(this.domainResponseMessageSender, never()).send(message);
        verify(this.scheduledTaskRepository, never()).delete(scheduledTask);

        // check if the scheduled time is updated to the message retry time
        assertEquals(new Timestamp(scheduledRetryTime.getTime()), scheduledTask.getscheduledTime());
    }

    /**
     * test processMessage with a scheduled task that failed
     */
    @Test
    public void testProcessScheduledMessageFailed() {
        final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        final Date scheduledRetryTime = calendar.getTime();

        // since the retryCount equals the maxRetries, the message should fail
        final RetryHeader retryHeader = new RetryHeader(this.getMaxRetryCount, this.getMaxRetryCount,
                scheduledRetryTime);

        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION).result(result)
                .dataObject(DATA_OBJECT).scheduled(true).retryHeader(retryHeader).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);

        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if message is send and task is deleted
        verify(this.domainResponseMessageSender, times(1)).send(message);
        verify(this.scheduledTaskRepository, times(1)).delete(scheduledTask);
    }

}
