/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.core.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;

/**
 * test class for DeviceResponseMessageService
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceResponseMessageServiceTest {

    @Mock
    private DomainResponseService domainResponseMessageSender;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @InjectMocks
    private DeviceResponseMessageService deviceResponseMessageService;

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
        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION)
                .result(ResponseMessageResultType.OK).dataObject(DATA_OBJECT).scheduled(true).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);
        scheduledTask.setPending();
        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if message is send and task is deleted
        verify(this.domainResponseMessageSender).send(message);
        verify(this.scheduledTaskRepository).delete(scheduledTask);
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
        final RetryHeader retryHeader = new RetryHeader(0, 1, scheduledRetryTime);

        final String exceptionMessage = "message";
        final OsgpException exception = new OsgpException(ComponentType.OSGP_CORE, exceptionMessage);

        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION).result(result)
                .dataObject(DATA_OBJECT).scheduled(true).retryHeader(retryHeader).osgpException(exception).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);

        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if the message is not send and the task is not deleted
        verify(this.domainResponseMessageSender, never()).send(message);
        verify(this.scheduledTaskRepository, never()).delete(scheduledTask);
        verify(this.scheduledTaskRepository).save(scheduledTask);

        // check if the scheduled time is updated to the message retry time
        assertEquals(new Timestamp(scheduledRetryTime.getTime()), scheduledTask.getscheduledTime());
        assertTrue(scheduledTask.getErrorLog().contains(exceptionMessage));
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
        final RetryHeader retryHeader = new RetryHeader(1, 1, scheduledRetryTime);

        final ProtocolResponseMessage message = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(DEVICE_MESSAGE_DATA).domain(DOMAIN).domainVersion(DOMAIN_VERSION).result(result)
                .dataObject(DATA_OBJECT).scheduled(true).retryHeader(retryHeader).build();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);

        when(this.scheduledTaskRepository.findByCorrelationUid(anyString())).thenReturn(scheduledTask);
        this.deviceResponseMessageService.processMessage(message);

        // check if message is send and task is deleted
        verify(this.domainResponseMessageSender).send(message);
        verify(this.scheduledTaskRepository).delete(scheduledTask);
    }

}
