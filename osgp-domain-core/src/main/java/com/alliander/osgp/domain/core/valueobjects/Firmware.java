/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.util.Date;

public class Firmware {

    private String deviceIdentification;

    private String firmwareVersion;

    private String description;

    private Date installationDate;

    private String installedBy;

    private boolean active;

    private DeviceModelFirmware deviceModelFirmware;

    public Firmware() {
        // Default constructor
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    public void setFirmwareVersion(final String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
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

    public void setInstallationDate(final Date installationDate) {
        this.installationDate = installationDate;
    }

    public String getInstalledBy() {
        return this.installedBy;
    }

    public void setInstalledBy(final String installedBy) {
        this.installedBy = installedBy;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public DeviceModelFirmware getDeviceModelFirmware() {
        return this.deviceModelFirmware;
    }

    public void setDeviceModelFirmware(final DeviceModelFirmware deviceModelFirmware) {
        this.deviceModelFirmware = deviceModelFirmware;
    }

}
