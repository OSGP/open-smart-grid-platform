/**
 * Copyright 2016 Smart Society Services B.V.
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

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * DeviceModelFirmware entity class holds information about the device model or type
 */
@Entity
public class DeviceModelFirmware extends AbstractEntity {

    private static final long serialVersionUID = 3479817855083883103L;

    @ManyToOne()
    @JoinColumn()
    private DeviceModel deviceModel;

    //private String deviceModelFirmwareId;

    @Column()
    private String filename;

    @Column(length = 15)
    private String modelCode;

    @Column(length = 100)
    private String description;

    @Column()
    private Boolean pushToNewDevices;

    @Column(length = 100)
    private String moduleVersionComm;

    @Column(length = 100)
    private String moduleVersionFunc;

    @Column(length = 100)
    private String moduleVersionMA;

    @Column(length = 100)
    private String moduleVersionMBus;

    @Column(length = 100)
    private String moduleVersionSec;

    public DeviceModelFirmware() {
        // Default constructor
    }

    public DeviceModelFirmware(final DeviceModel deviceModel, final String filename, final String modelCode, final String description,
            final Boolean pushToNewDevices, final String moduleVersionComm, final String moduleVersionFunc,
            final String moduleVersionMA, final String moduleVersionMBus, final String moduleVersionSec) {

        this.deviceModel = deviceModel;
        this.filename = filename;
        this.modelCode = modelCode;
        this.description = description;
        this.pushToNewDevices = pushToNewDevices;
        this.moduleVersionComm = moduleVersionComm;
        this.moduleVersionFunc = moduleVersionFunc;
        this.moduleVersionMA = moduleVersionMA;
        this.moduleVersionMBus = moduleVersionMBus;
        this.moduleVersionSec = moduleVersionSec;
    }

    public DeviceModel getDeviceModel() {
        return this.deviceModel;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getModelCode() {
        return this.modelCode;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getPushToNewDevices() {
        return this.pushToNewDevices;
    }

    public String getModuleVersionComm() {
        return this.moduleVersionComm;
    }

    public String getModuleVersionFunc() {
        return this.moduleVersionFunc;
    }

    public String getModuleVersionMA() {
        return this.moduleVersionMA;
    }

    public String getModuleVersionMBus() {
        return this.moduleVersionMBus;
    }

    public String getModuleVersionSec() {
        return this.moduleVersionSec;
    }

}
