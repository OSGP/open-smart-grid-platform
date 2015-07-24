/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class DlmsDevice extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3899692163578950343L;

    @Column(unique = true, nullable = true)
    private String deviceUid;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    private String deviceType;

    @Column(nullable = true, length = 255)
    private String publicKey;

    public DlmsDevice() {
        // Default constructor
    }

    public DlmsDevice(final String deviceUid, final String deviceIdentification, final String deviceType) {
        this.deviceUid = deviceUid;
        this.deviceIdentification = deviceIdentification;
        this.deviceType = deviceType;
    }

    public DlmsDevice(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceUid() {
        return this.deviceUid;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public boolean isPublicKeyPresent() {
        return !StringUtils.isBlank(this.publicKey);
    }

    public void updateRegistrationData(final byte[] deviceUid, final String deviceType) {
        this.deviceUid = Base64.encodeBase64String(deviceUid);
        this.deviceType = deviceType;
    }

    public void updatePublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    public void revokePublicKey() {
        this.publicKey = null;
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
        if (this.deviceType != null ? !this.deviceType.equals(device.deviceType) : device.deviceType != null) {
            return false;
        }
        if (this.deviceUid != null ? !this.deviceUid.equals(device.deviceUid) : device.deviceUid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.deviceUid != null ? this.deviceUid.hashCode() : 0;
        result = 31 * result + (this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0);
        result = 31 * result + (this.deviceType != null ? this.deviceType.hashCode() : 0);
        return result;
    }
}
