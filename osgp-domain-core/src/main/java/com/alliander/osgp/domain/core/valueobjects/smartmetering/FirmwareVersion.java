/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class FirmwareVersion implements Serializable {

    private static final long serialVersionUID = 892449074530829565L;

    private FirmwareModuleType type;
    private String version;

    public FirmwareVersion(final FirmwareModuleType type, final String version) {
        this.type = type;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("[%s => %s]", this.type, this.version);
    }

    public FirmwareModuleType getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }
}
