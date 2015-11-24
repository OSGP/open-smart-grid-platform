/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * @author OSGP
 * @deprecated temporary GAS meter administration
 */
@Deprecated
public class GASMeterDevice implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3526823976188640681L;

    private final String deviceIdentification;

    private final String smartMeterId;

    private final short channel;

    private final boolean wired;

    public GASMeterDevice(final String deviceIdentification, final String smartMeterId, final short channel,
            final boolean wired) {
        super();
        this.deviceIdentification = deviceIdentification;
        this.smartMeterId = smartMeterId;
        this.channel = channel;
        this.wired = wired;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getSmartMeter() {
        return this.smartMeterId;
    }

    public short getChannel() {
        return this.channel;
    }

    public boolean isWired() {
        return this.wired;
    }

}
