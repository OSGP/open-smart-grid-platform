/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileGenericDataResponse extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -156966569210717657L;

    private final ProfileGenericData profileGenericData;

    public ProfileGenericDataResponse(final ObisCodeValues logicalName, final List<CaptureObject> captureObjects,
            final List<ProfileEntry> profileEntries) {
        super();
        this.profileGenericData = new ProfileGenericData(logicalName, captureObjects, profileEntries);
    }

    public ProfileGenericData getProfileGenericData() {
        return this.profileGenericData;
    }

    public ObisCodeValues getLogicalName() {
        return this.profileGenericData.getLogicalName();
    }

    public List<CaptureObject> getCaptureObjects() {
        return this.profileGenericData.getCaptureObjects();
    }

    public List<ProfileEntry> getProfileEntries() {
        return this.profileGenericData.getProfileEntries();
    }
}
