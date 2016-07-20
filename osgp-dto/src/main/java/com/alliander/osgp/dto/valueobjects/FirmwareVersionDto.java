/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class FirmwareVersionDto implements Serializable {
    private static final long serialVersionUID = 4842058824665590962L;

    private FirmwareModuleType firmwareModuleType;
    private String version;

    public FirmwareVersionDto(final FirmwareModuleType firmwareModuleType, final String version) {
        this.firmwareModuleType = firmwareModuleType;
        this.version = version;
    }

    public FirmwareModuleType getFirmwareModuleType() {
        return this.firmwareModuleType;
    }

    public void setFirmwareModuleType(final FirmwareModuleType firmwareModuleType) {
        this.firmwareModuleType = firmwareModuleType;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
}
