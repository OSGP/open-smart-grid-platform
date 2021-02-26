/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class CleanUpMbusDeviceByChannelRequestDataDto implements Serializable, ActionRequestDto {

    private static final long serialVersionUID = -1687017075412242848L;

    private final Short channel;

    public CleanUpMbusDeviceByChannelRequestDataDto(final Short channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "CleanUpMbusDeviceByChannelDto [channel=" + this.channel + "]";
    }

    public Short getChannel() {
        return this.channel;
    }

}
