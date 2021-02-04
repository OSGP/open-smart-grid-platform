package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ActualPowerQualityResponse extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -156966569210717657L;

    private final ObisCodeValues logicalName;
    private final List<CaptureObject> captureObjects;
    private final List<ProfileEntry> profileEntries;

    public ActualPowerQualityResponse(final ObisCodeValues logicalName,
            final List<CaptureObject> captureObjects, final List<ProfileEntry> profileEntries) {
        super();
        this.logicalName = logicalName;
        this.captureObjects = captureObjects;
        this.profileEntries = profileEntries;
    }

    public ObisCodeValues getLogicalName() {
        return this.logicalName;
    }

    public List<CaptureObject> getCaptureObjects() {
        return this.captureObjects;
    }

    public List<ProfileEntry> getProfileEntries() {
        return this.profileEntries;
    }
}
