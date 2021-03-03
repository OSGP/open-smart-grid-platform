/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class DeCoupleMbusDeviceByChannelResponse extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -7800915379658671321L;

    private final Short channel;
    private final String mbusDeviceIdentification;

    public DeCoupleMbusDeviceByChannelResponse(final String mbusDeviceIdentification, final Short channel) {
        super();
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "DeCoupleMbusDeviceByChannelResponseData [channel=" + this.channel + ", mbusDeviceIdentification="
                + this.mbusDeviceIdentification + "]";
    }

    public Short getChannel() {
        return this.channel;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }
}
