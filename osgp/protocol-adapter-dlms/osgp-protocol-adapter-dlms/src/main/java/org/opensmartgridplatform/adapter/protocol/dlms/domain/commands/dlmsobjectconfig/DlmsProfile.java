package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.List;

public class DlmsProfile extends DlmsObject {

    private final List<DlmsCaptureObject> captureObjects;
    private final ProfileCaptureTime captureTime;
    private final Medium medium;

    public DlmsProfile(final DlmsObjectType type, final String obisCode, final List<DlmsCaptureObject> captureObjects,
            final ProfileCaptureTime captureTime, final Medium medium) {
        super(type, 7, obisCode);
        this.captureObjects = captureObjects;
        this.captureTime = captureTime;
        this.medium = medium;
    }

    public List<DlmsCaptureObject> getCaptureObjects() {
        return this.captureObjects;
    }

    public ProfileCaptureTime getCaptureTime() {
        return this.captureTime;
    }

    public Medium getMedium() {
        return this.medium;
    }
}
