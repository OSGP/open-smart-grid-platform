/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.core.application.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import com.alliander.osgp.core.application.config.SchedulingConfig;
import com.alliander.osgp.core.application.services.DeviceRequestMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.valueobjects.ScheduledTaskStatusType;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

/**
 * test class for ScheduledTaskScheduler
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduledTaskSchedulerTest {

    @Mock
    private DeviceRequestMessageService deviceRequestMessageService;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private ScheduledTaskScheduler scheduler;

    @Mock
    private SchedulingConfig schedulingConfig;

    private static final DeviceMessageMetadata DEVICE_MESSAGE_DATA = new DeviceMessageMetadata("deviceId",
            "organisationId", "correlationId", "messageType", 4);
    private static final String DOMAIN = "Domain";
    private static final String DATA_OBJECT = "data object";
    private static final Timestamp SCHEDULED_TIME = new Timestamp(Calendar.getInstance().getTime().getTime());

    /**
     * Test the scheduled task runner for the case when the
     * deviceRequestMessageService gives a functional exception
     *
     * @throws FunctionalException
     * @throws UnknownHostException
     */
    @Test
    public void testRunFunctionalException() throws FunctionalException, UnknownHostException {
        final List<ScheduledTask> scheduledTasks = new ArrayList<>();
        final ScheduledTask scheduledTask = new ScheduledTask(DEVICE_MESSAGE_DATA, DOMAIN, DOMAIN, DATA_OBJECT,
                SCHEDULED_TIME);
        scheduledTasks.add(scheduledTask);

        when(this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(any(ScheduledTaskStatusType.class),
                any(Timestamp.class), any(Pageable.class))).thenReturn(scheduledTasks)
                        .thenReturn(new ArrayList<ScheduledTask>());

        final Device device = new Device();
        device.updateRegistrationData(InetAddress.getByName("127.0.0.1"), "deviceType");
        when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
        when(this.scheduledTaskRepository.save(any(ScheduledTask.class))).thenReturn(scheduledTask);
        when(this.schedulingConfig.scheduledTaskPageSize()).thenReturn(30);
        doThrow(new FunctionalException(FunctionalExceptionType.ARGUMENT_NULL, ComponentType.OSGP_CORE))
                .when(this.deviceRequestMessageService).processMessage(any(ProtocolRequestMessage.class));

        this.scheduler.run();

        // check if task is deleted
        verify(this.scheduledTaskRepository).delete(scheduledTask);

    }

}
