/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class DlmsDevice extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3899692163578950343L;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column
    private String communicationMethod;

    @Column
    private String communicationProvider;

    @Column
    private String iccId;

    @Column
    private boolean hls3Active;

    @Column
    private boolean hls4Active;

    @Column
    private boolean hls5Active;

    @OneToMany(mappedBy = "dlmsDevice", cascade = CascadeType.PERSIST)
    private List<SecurityKey> securityKeys = new ArrayList<>();

    @Transient
    private String ipAddress;

    public DlmsDevice() {
        // Default constructor
    }

    public DlmsDevice(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public String toString() {
        return String.format("DlmsDevice[deviceId=%s, hls3=%b, hls4=%b, hls5=%b, ipAddress=%s]",
                this.deviceIdentification, this.hls3Active, this.hls4Active, this.hls5Active, this.ipAddress);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final DlmsDevice device = (DlmsDevice) o;

        if (this.deviceIdentification != null ? !this.deviceIdentification.equals(device.deviceIdentification)
                : device.deviceIdentification != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int result = 31 * (this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0);
        return result;
    }

    public String getCommunicationMethod() {
        return this.communicationMethod;
    }

    public void setCommunicationMethod(final String communicationMethod) {
        this.communicationMethod = communicationMethod;
    }

    public String getCommunicationProvider() {
        return this.communicationProvider;
    }

    public void setCommunicationProvider(final String communicationProvider) {
        this.communicationProvider = communicationProvider;
    }

    public void setIccId(final String value) {
        this.iccId = value;
    }

    public String getIccId() {
        return this.iccId;
    }

    public boolean isHls3Active() {
        return this.hls3Active;
    }

    public void setHls3Active(final boolean hls3Active) {
        this.hls3Active = hls3Active;
    }

    public boolean isHls4Active() {
        return this.hls4Active;
    }

    public void setHls4Active(final boolean hls4Active) {
        this.hls4Active = hls4Active;
    }

    public boolean isHls5Active() {
        return this.hls5Active;
    }

    public void setHls5Active(final boolean hls5Active) {
        this.hls5Active = hls5Active;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<SecurityKey> getSecurityKeys() {
        return this.securityKeys;
    }

    public void addSecurityKey(final SecurityKey securityKey) {
        this.securityKeys.add(securityKey);
        if (securityKey.getDlmsDevice() == null) {
            securityKey.setDlmsDevice(this);
        }
    }

    /**
     * The IP address is not part of the data in the protocol adapter database.
     * The value needs to have been set based on information from the core
     * database before it can be used.
     *
     * @return the device's network address, if it has been explicitly set;
     *         otherwise {@code null}.
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
