/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.services;

public class FirmwareFileRequest {
    private final String description;
    private final String fileName;
    private final boolean pushToNewDevices;
    private final boolean active;

    public FirmwareFileRequest(final String description, final String fileName, final boolean pushToNewDevices,
            final boolean active) {
        this.description = description;
        this.fileName = fileName;
        this.pushToNewDevices = pushToNewDevices;
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isPushToNewDevices() {
        return this.pushToNewDevices;
    }

    public boolean isActive() {
        return this.active;
    }
}
