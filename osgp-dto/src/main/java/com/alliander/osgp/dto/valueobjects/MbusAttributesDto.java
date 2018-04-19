/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class MbusAttributesDto implements Serializable {

    private static final long serialVersionUID = 454136243796705695L;

    private MbusAttributes mbusAttributes;
    private Long version;

    public MbusAttributesDto(final MbusAttributes mbusAttributes, final Long version) {
        this.mbusAttributes = mbusAttributes;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("ScanMbusChannelsRequestDataDto[%s => %s]", this.mbusAttributes, this.version);
    }

    public MbusAttributes getFirmwareModuleType() {
        return this.mbusAttributes;
    }

    public Long getVersion() {
        return this.version;
    }

}
