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

// ProfileGenericDataResponse
public class GetPowerQualityProfileResponseData extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -156966569210717657L;

    private final PowerQualityProfileData powerQualityProfileData;

    public GetPowerQualityProfileResponseData(final ObisCodeValues logicalName, final List<CaptureObject> captureObjects,
            final List<ProfileEntry> profileEntries) {
        super();
        this.powerQualityProfileData = new PowerQualityProfileData(logicalName, captureObjects, profileEntries);
    }

    public PowerQualityProfileData getPowerQualityProfileData() {
        return this.powerQualityProfileData;
    }

    public ObisCodeValues getLogicalName() {
        return this.powerQualityProfileData.getLogicalName();
    }

    public List<CaptureObject> getCaptureObjects() {
        return this.powerQualityProfileData.getCaptureObjects();
    }

    public List<ProfileEntry> getProfileEntries() {
        return this.powerQualityProfileData.getProfileEntries();
    }
}
