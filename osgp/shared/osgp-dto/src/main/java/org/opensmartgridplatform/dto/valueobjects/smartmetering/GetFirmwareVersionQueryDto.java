/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetFirmwareVersionQueryDto implements Serializable {

    private static final long serialVersionUID = 3789304613810680161L;

    private final ChannelDto channel;

    public GetFirmwareVersionQueryDto() {
        this(null);
    }

    public GetFirmwareVersionQueryDto(final ChannelDto channel) {
        this.channel = channel;
    }

    public boolean isMbusQuery() {
        return this.channel != null;
    }

    public ChannelDto getChannel() {
        return this.channel;
    }

}
