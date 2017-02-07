/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

public class ProfileGenericDataResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -156966569210717654L;

    private final ObisCodeValuesDto logicalName;
    private final List<CaptureObjectDto> captureObject;
    private final List<ProfileEntryDto> profileEntries;

    public ProfileGenericDataResponseDto(ObisCodeValuesDto logicalName, List<CaptureObjectDto> captureObject,
            List<ProfileEntryDto> profileEntries) {
        super();
        this.logicalName = logicalName;
        this.captureObject = captureObject;
        this.profileEntries = profileEntries;
    }

    public ObisCodeValuesDto getLogicalName() {
        return this.logicalName;
    }

    public List<CaptureObjectDto> getCaptureObject() {
        return this.captureObject;
    }

    public List<ProfileEntryDto> getProfileEntries() {
        return this.profileEntries;
    }

}
