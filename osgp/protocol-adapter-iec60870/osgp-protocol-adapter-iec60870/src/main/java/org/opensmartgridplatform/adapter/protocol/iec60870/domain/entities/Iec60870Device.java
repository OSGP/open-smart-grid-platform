/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec60870_device")
public class Iec60870Device extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8793285954115476857L;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column
    private Integer port;

    public Iec60870Device() {
        // Default constructor for Hibernate
    }

    public Iec60870Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    @Override
    public String toString() {
        return String.format("Iec60870Device[deviceId=%s, port=%s]", this.deviceIdentification, this.port);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Iec60870Device)) {
            return false;
        }

        final Iec60870Device device = (Iec60870Device) o;

        return Objects.equals(this.deviceIdentification, device.deviceIdentification);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.deviceIdentification);
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

}
