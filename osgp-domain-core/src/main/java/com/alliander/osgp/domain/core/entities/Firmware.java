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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * Firmware entity class
 */
@Entity
public class Firmware extends AbstractEntity {

    private static final long serialVersionUID = 5003530514434626119L;

    @ManyToOne()
    @JoinColumn
    private DeviceModel deviceModel;

    @Column(nullable = false)
    private int firmwareVersion;

    @Column(length = 255)
    private String description;

    @Lob
    @Fetch(FetchMode.SELECT)
    @Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
    byte[] installationFile;



    @Column()
    private Date installationDate;

    @Column()
    private String installedBy;

    @Column()
    private boolean active;

    @ManyToOne()
    @JoinColumn()
    private DeviceModelFirmware deviceModelFirmware;



    public Firmware(final DeviceModel deviceModel, final int firmwareVersion, final String description) {
        this.deviceModel = deviceModel;
        this.firmwareVersion = firmwareVersion;
        this.description = description;
    }

    public DeviceModel getDeviceModel() {
        return this.deviceModel;
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

    public byte[] getInstallationFile() {
        return this.installationFile;
    }

    public void setInstallationFile(final byte[] installationFile) {
        this.installationFile = installationFile;
    }

}
