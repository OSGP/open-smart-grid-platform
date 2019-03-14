/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class SecurityKey extends AbstractEntity {

    private static final long serialVersionUID = 2664922854864532720L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dlms_device_id")
    private DlmsDevice dlmsDevice;

    @Enumerated(EnumType.STRING)
    private SecurityKeyType securityKeyType;

    /**
     * Security keys with a null value for validFrom are keys that have not yet
     * or not successfully been set on the meter. When set on the meter this
     * value should immediately be updated.
     */
    @Column()
    private Date validFrom;

    @Column()
    private Date validTo;

    @Column(name = "security_key")
    private String key;

    public SecurityKey() {
        // Default constructor
    }

    public SecurityKey(final DlmsDevice dlmsDevice, final SecurityKeyType securityKeyType, final String key,
            final Date validFrom, final Date validTo) {
        this.dlmsDevice = dlmsDevice;
        this.securityKeyType = securityKeyType;
        if (validFrom != null) {
            this.validFrom = new Date(validFrom.getTime());
        }
        if (validTo != null) {
            this.validTo = new Date(validTo.getTime());
        }
        this.key = key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final SecurityKey compareKey = (SecurityKey) o;
        return Objects.equals(this.getId(), compareKey.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDlmsDevice(), this.getSecurityKeyType(), this.getId());
    }

    public DlmsDevice getDlmsDevice() {
        return this.dlmsDevice;
    }

    public SecurityKeyType getSecurityKeyType() {
        return this.securityKeyType;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(final Date validFrom) {
        this.validFrom = new Date(validFrom.getTime());
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public void setValidTo(final Date validTo) {
        if (validTo != null) {
            this.validTo = new Date(validTo.getTime());
        } else {
            this.validTo = null;
        }
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return "SecurityKey[type=" + this.securityKeyType + ", validFrom=" + this.validFrom + ", validTo="
                + this.validTo + ", key=" + this.key + ']';
    }

}
