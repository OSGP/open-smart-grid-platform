/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities;

import java.security.SecureRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class OslpDevice extends AbstractEntity {

    /**
     * Device type indicator for PSLD
     */
    public static final String PSLD_TYPE = "PSLD";

    /**
     * Device type indicator for SSLD
     */
    public static final String SSLD_TYPE = "SSLD";

    private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3899692663578950343L;

    @Column(unique = true, nullable = true)
    private String deviceUid;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    private String deviceType;

    @Column(nullable = true)
    private Integer sequenceNumber;

    @Column(nullable = true)
    private Integer randomDevice;

    @Column(nullable = true)
    private Integer randomPlatform;

    @Transient
    private final SecureRandom random = new SecureRandom();

    @Column(nullable = true, length = 255)
    private String publicKey;

    public OslpDevice() {
        // Default constructor
    }

    public OslpDevice(final String deviceUid, final String deviceIdentification, final String deviceType) {
        this.deviceUid = deviceUid;
        this.deviceIdentification = deviceIdentification;
        this.deviceType = deviceType;
    }

    public OslpDevice(final String deviceIdentification) {
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

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(final Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getRandomDevice() {
        return this.randomDevice;
    }

    public void setRandomDevice(final Integer randomDevice) {
        this.randomDevice = randomDevice;
    }

    public Integer getRandomPlatform() {
        return this.randomPlatform;
    }

    public void setRandomPlatform(final Integer randomPlatform) {
        this.randomPlatform = randomPlatform;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public boolean isPublicKeyPresent() {
        return !StringUtils.isBlank(this.publicKey);
    }

    public void updateRegistrationData(final byte[] deviceUid, final String deviceType, final Integer randomDevice) {
        this.deviceUid = Base64.encodeBase64String(deviceUid);
        this.deviceType = deviceType;
        // Save secure device random.
        this.randomDevice = randomDevice;
        // Generate secure platform random.
        this.randomPlatform = this.random.nextInt(SEQUENCE_NUMBER_MAXIMUM + 1);
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
        final OslpDevice device = (OslpDevice) o;
        if (this.deviceIdentification != null ? !this.deviceIdentification.equals(device.deviceIdentification)
                : device.deviceIdentification != null) {
            return false;
        }
        if (this.deviceType != null ? !this.deviceType.equals(device.deviceType) : device.deviceType != null) {
            return false;
        }
        return !(this.deviceUid != null ? !this.deviceUid.equals(device.deviceUid) : device.deviceUid != null);
    }

    @Override
    public int hashCode() {
        int result = this.deviceUid != null ? this.deviceUid.hashCode() : 0;
        result = 31 * result + (this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0);
        result = 31 * result + (this.deviceType != null ? this.deviceType.hashCode() : 0);
        return result;
    }
}
