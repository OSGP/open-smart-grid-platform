/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.opensmartgridplatform.domain.microgrids.valueobjects.TaskStatusType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class Task extends AbstractEntity {

    private static final long serialVersionUID = -1231883978623839983L;

    @Column
    private String taskIdentification;

    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private TaskStatusType taskStatus;

    @Column
    private Date startTime;

    @Column
    private Date endTime;

    protected Task() {
        // default constructor for Hibernate
    }

    public Task(final String taskIdentification) {
        this.taskIdentification = taskIdentification;
        this.taskStatus = TaskStatusType.AVAILABLE;
    }

    public String getTaskIdentification() {
        return this.taskIdentification;
    }

    public TaskStatusType getTaskStatus() {
        return this.taskStatus;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void start() {
        this.startTime = new Date();
        this.taskStatus = TaskStatusType.RUNNING;
    }

    public void finish() {
        this.endTime = new Date();
        this.taskStatus = TaskStatusType.AVAILABLE;
    }
}
