/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * Firmware entity class holds information about the device model or type
 */
@Entity
public class Firmware extends AbstractEntity {

    private static final long serialVersionUID = 3479817855083883103L;

    @ManyToOne()
    @JoinColumn(name = "device_model_id")
    private DeviceModel deviceModel;

    @Column()
    private String filename;

    @Column(length = 100)
    private String description;

    @Column()
    private boolean pushToNewDevices;

    @Column(length = 100)
    private String moduleVersionComm;

    @Column(length = 100)
    private String moduleVersionFunc;

    @Column(length = 100)
    private String moduleVersionMa;

    @Column(length = 100)
    private String moduleVersionMbus;

    @Column(length = 100)
    private String moduleVersionSec;

    @Lob
    @Column()
    private byte file[];

    @Column()
    private String hash;

    public Firmware() {
        // Default constructor
    }

    public Firmware(final DeviceModel deviceModel, final String filename, final String description,
            final boolean pushToNewDevices, final FirmwareModuleData firmwareModuleData) {
        this.deviceModel = deviceModel;
        this.filename = filename;
        this.description = description;
        this.pushToNewDevices = pushToNewDevices;
        this.updateFirmwareModuleData(firmwareModuleData);
    }

    public Firmware(final DeviceModel deviceModel, final String filename, final String description,
            final boolean pushToNewDevices, final FirmwareModuleData firmwareModuleData, final byte[] file,
            final String hash) {
        this(deviceModel, filename, description, pushToNewDevices, firmwareModuleData);
        this.file = file;
        this.hash = hash;
    }

    public void updateFirmwareModuleData(final FirmwareModuleData firmwareModuleData) {
        this.moduleVersionComm = firmwareModuleData.getModuleVersionComm();
        this.moduleVersionFunc = firmwareModuleData.getModuleVersionFunc();
        this.moduleVersionMa = firmwareModuleData.getModuleVersionMa();
        this.moduleVersionMbus = firmwareModuleData.getModuleVersionMbus();
        this.moduleVersionSec = firmwareModuleData.getModuleVersionSec();
    }

    public DeviceModel getDeviceModel() {
        return this.deviceModel;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getPushToNewDevices() {
        return this.pushToNewDevices;
    }

    public String getModuleVersionComm() {
        return this.moduleVersionComm;
    }

    public String getModuleVersionFunc() {
        return this.moduleVersionFunc;
    }

    public String getModuleVersionSec() {
        return this.moduleVersionSec;
    }

    public String getModuleVersionMa() {
        return this.moduleVersionMa;
    }

    public String getModuleVersionMbus() {
        return this.moduleVersionMbus;
    }

    public void setDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setPushToNewDevices(final boolean pushToNewDevices) {
        this.pushToNewDevices = pushToNewDevices;
    }

    public void setModuleVersionComm(final String moduleVersionComm) {
        this.moduleVersionComm = moduleVersionComm;
    }

    public void setModuleVersionFunc(final String moduleVersionFunc) {
        this.moduleVersionFunc = moduleVersionFunc;
    }

    public void setModuleVersionMa(final String moduleVersionMa) {
        this.moduleVersionMa = moduleVersionMa;
    }

    public void setModuleVersionMbus(final String moduleVersionMbus) {
        this.moduleVersionMbus = moduleVersionMbus;
    }

    public void setModuleVersionSec(final String moduleVersionSec) {
        this.moduleVersionSec = moduleVersionSec;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getFile() {
        return this.file;
    }

    public void setFile(final byte[] file) {
        this.file = file;
    }
}
