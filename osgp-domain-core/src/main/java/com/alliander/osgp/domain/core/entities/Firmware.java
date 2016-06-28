/**
 * Copyright 2016 Smart Society Services B.V.
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
 * Firmware entity class
 */
@Entity
public class Firmware extends AbstractEntity {

    private static final long serialVersionUID = 5003530514434626119L;

    @Column(nullable = false)
    private int firmwareVersion;

    @Column(length = 255)
    private String description;

    @Column()
    private Date installationDate;

    @Column()
    private String installedBy;

    @Column()
    private boolean active;

    @ManyToOne()
    @JoinColumn()
    private DeviceModelFirmware deviceModelFirmware;

    @ManyToOne()
    @JoinColumn()
    private Device device;

    public Firmware() {
        // Default constructor for hibernate
    }

    public Firmware(final int firmwareVersion, final String description) {
        this.firmwareVersion = firmwareVersion;
        this.description = description;
    }

    public int getFirmwareVersion() {
        return this.firmwareVersion;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
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
