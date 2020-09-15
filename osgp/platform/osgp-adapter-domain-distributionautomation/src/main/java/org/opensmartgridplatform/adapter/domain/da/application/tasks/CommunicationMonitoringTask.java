/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.tasks;

import java.util.List;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.domain.da.application.services.CommunicationRecoveryService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.entities.Task;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.TaskRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.TaskStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunicationMonitoringTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringTask.class);

    private static final String TASK_IDENTIFICATION = "DACommunicationMonitoring";

    private static final String DOMAIN = "DISTRIBUTION_AUTOMATION";
    private static final String DOMAIN_VERSION = "1.0";

    @Autowired
    private CommunicationRecoveryService communicationRecoveryService;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Autowired
    private Integer minimumTimeBetweenRuns;

    @Autowired
    private Integer maximumTimeWithoutCommunication;

    @Override
    public void run() {
        LOGGER.info("Running communication monitoring task.");

        Task task = this.loadTask();

        if (this.taskAlreadyRunning(task)) {
            LOGGER.info("Communication Monitoring Task already running. Skipping this run.");
            return;
        }

        if (this.taskAlreadyRan(task)) {
            LOGGER.info(
                    "Communication Monitoring Task already ran within minimum time between runs. Skipping this run.");
            return;
        }

        task = this.startTask(task);

        final List<RtuDevice> rtuDevices = this.loadDevices(task);
        LOGGER.info("Found {} device(s) for which communication should be restored.", rtuDevices.size());

        for (final RtuDevice rtu : rtuDevices) {
            LOGGER.debug("Restoring communication for device {}.", rtu.getDeviceIdentification());

            this.communicationRecoveryService.signalConnectionLost(rtu);
            this.communicationRecoveryService.restoreCommunication(rtu);
        }

        this.finishTask(task);
    }

    private Task loadTask() {
        LOGGER.debug("Loading task from repository.");
        Task task = this.taskRepository.findByTaskIdentification(TASK_IDENTIFICATION);
        if (task == null) {
            LOGGER.info("No existing task found, creating new task");
            task = this.createNewTask();
        }
        return task;
    }

    private Task createNewTask() {
        LOGGER.debug("Creating new task.");
        Task task = new Task(TASK_IDENTIFICATION);
        task = this.taskRepository.save(task);
        return task;
    }

    private boolean taskAlreadyRunning(final Task task) {
        LOGGER.debug("Checking if task is already running.");
        return TaskStatusType.RUNNING.equals(task.getTaskStatus());
    }

    private boolean taskAlreadyRan(final Task task) {
        LOGGER.debug("Checking if task has already ran within minimum time between runs.");

        if (task.getEndTime() == null) {
            return false;
        }

        final DateTime now = DateTime.now();
        final DateTime taskEndTime = new DateTime(task.getEndTime());
        return now.isBefore(taskEndTime.plusMinutes(this.minimumTimeBetweenRuns));
    }

    private Task startTask(final Task task) {
        LOGGER.debug("Starting task.");
        task.start();
        return this.taskRepository.save(task);
    }

    private Task finishTask(final Task task) {
        LOGGER.debug("Finishing task.");
        task.finish();
        return this.taskRepository.save(task);
    }

    private List<RtuDevice> loadDevices(final Task task) {
        LOGGER.debug("Loading devices from repository for which communication should be restored.");
        final DateTime lastCommunicationTime = new DateTime(task.getStartTime())
                .minusMinutes(this.maximumTimeWithoutCommunication);
        final DomainInfo domainInfo = this.domainInfoRepository.findByDomainAndDomainVersion(DOMAIN, DOMAIN_VERSION);
        return this.rtuDeviceRepository.findByDeviceLifecycleStatusAndLastCommunicationTimeBeforeAndDomainInfo(
                DeviceLifecycleStatus.IN_USE, lastCommunicationTime.toDate(), domainInfo);
    }
}
