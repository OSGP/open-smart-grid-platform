/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class FirmwareFileDto implements Serializable {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -4794626243032507358L;

    private String firmwareIdentification;
    private byte[] firmwareFile;

    public FirmwareFileDto(final String firmwareIdentification, final byte[] firmwareFile) {
        this.firmwareIdentification = firmwareIdentification;
        this.firmwareFile = firmwareFile;
    }

    public String getFirmwareIdentification() {
        return this.firmwareIdentification;
    }

    public byte[] getFirmwareFile() {
        return this.firmwareFile.clone();
    }
}
