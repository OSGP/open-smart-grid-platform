/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * @author OSGP
 * @deprecated temporary GAS meter administration
 */
@Entity
@Deprecated
public class GASMeterDevice extends AbstractEntity {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3526823976188640681L;

    @Identification
    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column(nullable = false, length = 40)
    private String smartMeterId;

    @Column(nullable = false)
    private short channel;

    @Column(nullable = false)
    private boolean wired;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getSmartMeterId() {
        return this.smartMeterId;
    }

    public void setSmartMeterId(final String smartMeterId) {
        this.smartMeterId = smartMeterId;
    }

    public short getChannel() {
        return this.channel;
    }

    public void setChannel(final short channel) {
        this.channel = channel;
    }

    public boolean isWired() {
        return this.wired;
    }

    public void setWired(final boolean wired) {
        this.wired = wired;
    }

}
