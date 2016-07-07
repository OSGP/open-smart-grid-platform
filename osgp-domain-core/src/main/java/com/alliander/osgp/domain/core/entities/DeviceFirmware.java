/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.domain.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * DeviceFirmware entity class
 */
@Entity
public class DeviceFirmware extends AbstractEntity {

    private static final long serialVersionUID = 5003530514434626119L;

    @Column()
    private Date installationDate;

    @Column()
    private String installedBy;

    @Column()
    private boolean active;

    @ManyToOne()
    @JoinColumn(name = "device_model_firmware_id")
    private DeviceModelFirmware deviceModelFirmware;

    @ManyToOne()
    @JoinColumn(name = "device_id")
    private Device device;

    public DeviceFirmware() {
        // Default constructor for hibernate
    }

    public Date getInstallationDate() {
        return this.installationDate;
    }

    public String getInstalledBy() {
        return this.installedBy;
    }

    public boolean isActive() {
        return this.active;
    }

    public DeviceModelFirmware getDeviceModelFirmware() {
        return this.deviceModelFirmware;
    }

    public Device getDevice() {
        return this.device;
    }

}
