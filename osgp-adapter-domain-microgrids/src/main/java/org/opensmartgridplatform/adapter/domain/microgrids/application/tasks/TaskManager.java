/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.tasks;

import java.util.Date;

import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.microgrids.entities.Task;
import org.opensmartgridplatform.domain.microgrids.repositories.TaskRepository;
import org.opensmartgridplatform.domain.microgrids.valueobjects.TaskStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class TaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

    private final String managedTaskIdentification;
    private final int minimumMinutesBetweenRuns;

    private final TaskRepository taskRepository;
    private Task task;

    public TaskManager(final TaskRepository taskRepository, final String taskIdentification,
            final int minimumTimeBetweenRunsMillis) {
        this.taskRepository = taskRepository;
        this.managedTaskIdentification = taskIdentification;
        this.minimumMinutesBetweenRuns = minimumTimeBetweenRunsMillis;
    }

    @Transactional("transactionManager")
    public boolean startTask() {
        this.loadTask();

        if (this.taskAlreadyRunning()) {
            LOGGER.info("Communication Monitoring Task already running. Skipping this run.");
            return false;
        }

        if (this.taskAlreadyRan()) {
            LOGGER.info(
                    "Communication Monitoring Task already ran within minimum time between runs. Skipping this run.");
            return false;
        }

        LOGGER.debug("Starting task.");
        this.task.start();
        this.task = this.taskRepository.save(this.task);

        return true;
    }

    private void loadTask() {
        LOGGER.debug("Loading task from repository.");
        this.task = this.taskRepository.findByTaskIdentification(this.managedTaskIdentification);
        if (this.task == null) {
            LOGGER.info("No existing task found, creating new task");
            this.createNewTask();
        }
    }

    private void createNewTask() {
        LOGGER.debug("Creating new task.");
        final Task newTask = new Task(this.managedTaskIdentification);
        this.task = this.taskRepository.save(newTask);
    }

    private boolean taskAlreadyRunning() {
        LOGGER.debug("Checking if task is already running.");
        return TaskStatusType.RUNNING == this.task.getTaskStatus();
    }

    private boolean taskAlreadyRan() {
        LOGGER.debug("Checking if task has already ran within " + this.minimumMinutesBetweenRuns
                + " (minimum time between runs in minutes).");

        if (this.task.getEndTime() == null) {
            return false;
        }

        final DateTime now = DateTime.now();
        final DateTime taskEndTime = new DateTime(this.task.getEndTime());
        return now.isBefore(taskEndTime.plusMinutes(this.minimumMinutesBetweenRuns));
    }

    @Transactional("transactionManager")
    public void finishTask() {
        LOGGER.debug("Finishing task.");
        this.task.finish();
        this.task = this.taskRepository.save(this.task);
    }

    public Date getTaskStartTime() {
        return this.task.getStartTime();
    }
}
