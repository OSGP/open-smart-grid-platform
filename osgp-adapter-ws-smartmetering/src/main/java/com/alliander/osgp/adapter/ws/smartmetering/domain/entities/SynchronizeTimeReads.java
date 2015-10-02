package com.alliander.osgp.adapter.ws.smartmetering.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@Entity
public class SynchronizeTimeReads extends AbstractEntity {

    private static final long serialVersionUID = -136966569210717654L;

    @Column
    private String deviceIdentification;

    @Column
    private String correlationUid;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public void setCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
    }
}