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

    private String type;
    private String version;

    public FirmwareVersionDto(final String type, final String version) {
        this.type = type;
        this.version = version;
    }

    public String getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }
}
