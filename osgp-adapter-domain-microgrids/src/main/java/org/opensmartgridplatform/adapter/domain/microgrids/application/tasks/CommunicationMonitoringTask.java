/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.tasks;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.domain.microgrids.application.services.CommunicationRecoveryService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunicationMonitoringTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringTask.class);

    private static final String TASK_IDENTIFICATION = "MicrogridsCommunicationMonitoring";

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommunicationRecoveryService communicationRecoveryService;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private Integer minimumTimeBetweenRuns;

    @Autowired
    private Integer maximumTimeWithoutCommunication;

    @Autowired
    private Long initialDelay;

    @Override
    public void run() {
        if (!this.hasInitialDelayExpired()) {
            LOGGER.info(
                    "Skipping communication monitoring task because the initial delay of {} milliseconds has not yet expired.",
                    this.initialDelay);
            return;
        }

        LOGGER.info("Running communication monitoring task.");

        final TaskManager taskManager = new TaskManager(this.taskRepository, TASK_IDENTIFICATION,
                this.minimumTimeBetweenRuns);
        final boolean taskStarted = taskManager.startTask();

        if (!taskStarted) {
            return;
        }

        final List<RtuDevice> rtuDevices = this.loadDevices(taskManager.getTaskStartTime());
        LOGGER.info("Found {} device(s) for which communication should be restored.", rtuDevices.size());

        for (final RtuDevice rtu : rtuDevices) {
            LOGGER.debug("Restoring communication for device {}.", rtu.getDeviceIdentification());

            this.communicationRecoveryService.signalConnectionLost(rtu);
            this.communicationRecoveryService.restoreCommunication(rtu);
        }

        taskManager.finishTask();
    }

    private List<RtuDevice> loadDevices(final Date taskStartTime) {
        LOGGER.debug("Loading devices from repository for which communication should be restored.");
        final DateTime lastCommunicationTime = new DateTime(taskStartTime)
                .minusMinutes(this.maximumTimeWithoutCommunication);
        return this.rtuDeviceRepository.findByDeviceLifecycleStatusAndLastCommunicationTimeBefore(
                DeviceLifecycleStatus.IN_USE, lastCommunicationTime.toDate());
    }

    /**
     * Determine the up time of the JVM and test if the initial delay has
     * expired.
     */
    protected boolean hasInitialDelayExpired() {
        final RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        return mx.getUptime() > this.initialDelay;
    }

}
