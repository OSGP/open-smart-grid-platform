/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

public class FirmwareVersionResponseDataDto extends ActionResponseDto {

    private static final long serialVersionUID = 4779593744529504287L;

    private List<FirmwareVersionDto> firmwareVersions;

    public FirmwareVersionResponseDataDto() {
        // Default constructor
    }

    public FirmwareVersionResponseDataDto(final List<FirmwareVersionDto> firmwareVersions) {
        super();
        this.firmwareVersions = firmwareVersions;
    }

    public List<FirmwareVersionDto> getFirmwareVersions() {
        return this.firmwareVersions;
    }

}