/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;

/**
 * DeviceFirmwareModuleVersionId holds the identification for a record of the
 * {@link DeviceCurrentFirmwareModuleVersion} class, which has a composite
 * identifier.
 */
public class DeviceFirmwareModuleVersionId implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -33634025846357516L;

    private Long deviceId;
    private String moduleDescription;
    private String moduleVersion;

    public Long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getModuleDescription() {
        return this.moduleDescription;
    }

    public void setModuleDescription(final String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public String getModuleVersion() {
        return this.moduleVersion;
    }

    public void setModuleVersion(final String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }
}
