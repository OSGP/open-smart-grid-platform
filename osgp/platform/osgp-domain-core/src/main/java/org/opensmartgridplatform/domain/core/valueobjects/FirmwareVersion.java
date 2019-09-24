/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class FirmwareVersion implements Serializable {

    private static final long serialVersionUID = 892449074530829565L;

    private final FirmwareModuleType type;
    private final String version;

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

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.version);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FirmwareVersion)) {
            return false;
        }
        final FirmwareVersion other = (FirmwareVersion) obj;
        return this.type == other.type && Objects.equals(this.version, other.version);
    }

}
