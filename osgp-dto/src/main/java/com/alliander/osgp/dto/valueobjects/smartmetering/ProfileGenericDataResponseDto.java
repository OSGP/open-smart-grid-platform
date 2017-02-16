/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class ProfileGenericDataResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -156966569210717654L;

    private final ObisCodeValuesDto logicalName;
    private final CaptureObjectsDto captureObjects;
    private final ProfileEntriesDto profileEntries;

    public ProfileGenericDataResponseDto(ObisCodeValuesDto logicalName, CaptureObjectsDto captureObjects,
            ProfileEntriesDto profileEntries) {
        super();
        this.logicalName = logicalName;
        this.captureObjects = captureObjects;
        this.profileEntries = profileEntries;
    }

    public ObisCodeValuesDto getLogicalName() {
        return this.logicalName;
    }

    public CaptureObjectsDto getCaptureObjects() {
        return this.captureObjects;
    }

    public ProfileEntriesDto getProfileEntries() {
        return this.profileEntries;
    }
}
