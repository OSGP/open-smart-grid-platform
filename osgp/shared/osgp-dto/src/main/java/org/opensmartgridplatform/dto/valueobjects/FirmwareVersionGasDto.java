/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class FirmwareVersionGasDto implements Serializable {

    private static final long serialVersionUID = -8072852452663009458L;

    private final FirmwareModuleType firmwareModuleType;
    private final String version;

    public FirmwareVersionGasDto(final FirmwareModuleType firmwareModuleType, final String version) {
        this.firmwareModuleType = firmwareModuleType;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("FirmwareVersionDto[%s => %s]", this.firmwareModuleType, this.version);
    }

    public FirmwareModuleType getFirmwareModuleType() {
        return this.firmwareModuleType;
    }

    public String getVersion() {
        return this.version;
    }
}
