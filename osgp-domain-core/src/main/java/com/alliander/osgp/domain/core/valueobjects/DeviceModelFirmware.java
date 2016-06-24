/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.util.Date;

public class DeviceModelFirmware {

    private int id;

    private String filename;

    private String modelCode;

    private String manufacturer;

    private String description;

    private Boolean pushToNewDevices;

    private String moduleVersionComm;

    private String moduleVersionFunc;

    private String moduleVersionMa;

    private String moduleVersionMbus;

    private String moduleVersionSec;

    private byte[] file;

    private Date creationTime;

    public DeviceModelFirmware() {
        // Default constructor
    }

    public DeviceModelFirmware(final String filename, final String manufacturer, final String modelCode, final String description, final boolean pushToNewDevices,
            final String moduleVersionComm, final String moduleVersionFunc, final String moduleVersionMA, final String moduleVersionMBus,
            final String moduleVersionSec, final byte[] file) {
        this.filename = filename;
        this.manufacturer = manufacturer;
        this.modelCode = modelCode;
        this.description = description;
        this.pushToNewDevices = pushToNewDevices;
        this.moduleVersionComm = moduleVersionComm;
        this.moduleVersionFunc = moduleVersionFunc;
        this.moduleVersionMa = moduleVersionMA;
        this.moduleVersionMbus = moduleVersionMBus;
        this.moduleVersionSec = moduleVersionSec;
        this.file = file;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getModelCode() {
        return this.modelCode;
    }

    public void setModelCode(final String modelCode) {
        this.modelCode = modelCode;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getPushToNewDevices() {
        return this.pushToNewDevices;
    }

    public void setPushToNewDevices(final Boolean pushToNewDevices) {
        this.pushToNewDevices = pushToNewDevices;
    }

    public String getModuleVersionComm() {
        return this.moduleVersionComm;
    }

    public void setModuleVersionComm(final String moduleVersionComm) {
        this.moduleVersionComm = moduleVersionComm;
    }

    public String getModuleVersionFunc() {
        return this.moduleVersionFunc;
    }

    public void setModuleVersionFunc(final String moduleVersionFunc) {
        this.moduleVersionFunc = moduleVersionFunc;
    }

    public String getModuleVersionMa() {
        return this.moduleVersionMa;
    }

    public void setModuleVersionMa(final String moduleVersionMa) {
        this.moduleVersionMa = moduleVersionMa;
    }

    public String getModuleVersionMbus() {
        return this.moduleVersionMbus;
    }

    public void setModuleVersionMbus(final String moduleVersionMbus) {
        this.moduleVersionMbus = moduleVersionMbus;
    }

    public String getModuleVersionSec() {
        return this.moduleVersionSec;
    }

    public void setModuleVersionSec(final String moduleVersionSec) {
        this.moduleVersionSec = moduleVersionSec;
    }

    public byte[] getFile() {
        return this.file;
    }

    public void setFile(final byte[] file) {
        this.file = file;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(final Date creationTime) {
        this.creationTime = creationTime;
    }

}
