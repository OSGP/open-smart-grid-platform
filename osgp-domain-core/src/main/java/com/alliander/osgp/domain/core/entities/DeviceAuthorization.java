/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class DeviceAuthorization extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1468328289658974067L;

    @ManyToOne()
    @JoinColumn()
    @Cascade(value = { CascadeType.ALL })
    private Device device;

    @ManyToOne()
    @JoinColumn()
    private Organisation organisation;

    @Column()
    private DeviceFunctionGroup functionGroup;

    public DeviceAuthorization() {
        // Default constructor
    }

    public DeviceAuthorization(final Device device, final Organisation organisation,
            final DeviceFunctionGroup functionGroup) {
        this.device = device;
        this.organisation = organisation;
        this.functionGroup = functionGroup;
    }

    public Device getDevice() {
        return this.device;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public DeviceFunctionGroup getFunctionGroup() {
        return this.functionGroup;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceAuthorization)) {
            return false;
        }
        final DeviceAuthorization authorization = (DeviceAuthorization) o;
        // Only comparing the device and organisation identifications (and not
        // the complete objects) to prevent stack
        // overflow errors when comparing devices (which contain device
        // authorizations).
        if (this.device != null ? !this.device.getDeviceIdentification().equals(
                authorization.device.getDeviceIdentification()) : authorization.device != null) {
            return false;
        }
        if (this.organisation != null ? !this.organisation.getOrganisationIdentification().equals(
                authorization.organisation.getOrganisationIdentification()) : authorization.organisation != null) {
            return false;
        }
        if (this.functionGroup != null ? !this.functionGroup.equals(authorization.functionGroup)
                : authorization.functionGroup != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.device != null ? this.device.getDeviceIdentification().hashCode() : 0;
        result = 31 * result
                + (this.organisation != null ? this.organisation.getOrganisationIdentification().hashCode() : 0);
        result = 31 * result + (this.functionGroup != null ? this.functionGroup.hashCode() : 0);
        return result;
    }
}
