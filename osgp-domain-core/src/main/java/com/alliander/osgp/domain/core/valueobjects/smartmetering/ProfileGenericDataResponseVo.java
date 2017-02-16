/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileGenericDataResponseVo extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -156966569210717657L;

    private final ObisCodeValues logicalName;
    private final List<CaptureObjectVo> captureObjects;
    private final List<ProfileEntryVo> profileEntries;

    public ProfileGenericDataResponseVo(ObisCodeValues logicalName, List<CaptureObjectVo> captureObjects,
            List<ProfileEntryVo> profileEntries) {
        super();
        this.logicalName = logicalName;
        this.captureObjects = captureObjects;
        this.profileEntries = profileEntries;
    }

    public ObisCodeValues getLogicalName() {
        return this.logicalName;
    }

    public List<CaptureObjectVo> getCaptureObjects() {
        return this.captureObjects;
    }

    public List<ProfileEntryVo> getProfileEntries() {
        return this.profileEntries;
    }

}
