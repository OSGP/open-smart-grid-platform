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
    @Column(nullable = true)
    private Date validFrom;

    @Column(nullable = true)
    private Date validTo;

    @Column(name = "security_key")
    private String key;

    @Column(nullable = true)
    private Integer invocationCounter;

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

    public int getInvocationCounter() {
        return this.invocationCounter == null ? 0 : this.invocationCounter;
    }

    public void setInvocationCounter(final Integer invocationCounter) {
        this.invocationCounter = invocationCounter;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecurityKey[type=");
        sb.append(this.securityKeyType).append(", validFrom=").append(this.validFrom).append(", validTo=")
                .append(this.validTo).append(", key=").append(this.key);
        if (this.invocationCounter != null) {
            sb.append(", ic=").append(this.invocationCounter);
        }
        return sb.append(']').toString();
    }

}
